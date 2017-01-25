import java.io.File;
import java.util.ArrayList;

public class proRAW {
	Util util;
	ArrayList<Image> images;
	ArrayList<ExposureChange> exposureChanges;
	ArrayList<TemperatureChange> temperatureChanges;
	String folder;
	double exposureIncrements;
	int startImage;
	int startLocation, endLocation;

	public proRAW() {
		util = new Util();
		images = new ArrayList<Image>();
		exposureChanges = new ArrayList<ExposureChange>();
		temperatureChanges = new ArrayList<TemperatureChange>();
		folder = "E:/Timelapse Library/Files/12-27-2016/Beach 1/";
		startImage = 8009;

		retrieveData();
		analyzeExposure();
		analyzeWB();
		printChanges();
//		updateMetadata();
		// util.displayInfo(images);
	}

	public void retrieveData() {
		// Starting file
		File file = new File(folder + "IMG_" + startImage + ".CR2");

		// Goes through every file in sequence
		while (file.exists()) {
			Image newImage = new Image(file.getName(), folder);
			newImage.setListNum(images.size());
			getImageData(newImage);
			images.add(newImage);

			newImage.printInfo();

			file = getNextImage(newImage);
		}
	}

	public File getNextImage(Image currIMG) {
		String imageName = currIMG.getName();

		String stringNum = "";
		String extension = "";

		// Grabs the image number and extension separately
		for (int i = 0; i < imageName.length(); i++) {
			if (Character.isDigit(imageName.charAt(i))) {
				stringNum += imageName.charAt(i);
			} else if (imageName.charAt(i) == '.') {
				for (int j = i; j < imageName.length(); j++) {
					extension += imageName.charAt(j);
				}
				break;
			}
		}
		// Increments image number. Uses it to get next file
		// name
		int nextNum = Integer.parseInt(stringNum) + 1;
		return new File(folder + "IMG_" + nextNum + extension);
	}

	public Image getPrevImage(Image currIMG) {
		return images.get(currIMG.getListNum() - 1);
	}

	public void getImageData(Image image) {
		// Gets data from xmp file
		File xmp = image.getXMP();

		// ***************** WHITE BALANCE *****************
		// =================================================================================================================

		// If white balance exists in the file.. set it
		if (util.getData(xmp).indexOf("Temperature") != -1) {
			int whiteBalance = (int) Double.parseDouble(util.find(xmp, "Temperature"));
			image.setWhiteBalance(whiteBalance);
		}
		// If it does not exist in file.. create white balance value
		else {
			int whiteBalance = getPrevImage(image).getWhiteBalance();
			image.setWhiteBalance(whiteBalance);

			String newData = util.createValue(xmp, "AutoWhiteVersion", "crs:Temperature=",
					Integer.toString(whiteBalance));

			util.writeFile(image, newData);
		}
		// ***************** EXPOSURE *****************
		// ==================================================================================================

		String shutterSpeed = util.find(xmp, "ExposureTime");
		image.setShutterSpeed(shutterSpeed);
		image.calcShutterNum();

		String apString = util.find(xmp, "FNumber");

		String leftA = apString.substring(0, apString.indexOf('/'));
		String rightA = apString.substring(leftA.length() + 1, apString.length());

		double aperture = Double.parseDouble(leftA) / Double.parseDouble(rightA);
		image.setAperture(aperture);

		String iso = util.find(xmp, "RecommendedExposureIndex");
		image.setISO(Integer.parseInt(iso));

		String exposureOffset = util.find(xmp, "Exposure2012");
		image.setExposureOffset(Double.parseDouble(exposureOffset));
	}

	public void analyzeExposure() {
		int startExpoChange = 0;

		for (int i = 0; i < images.size() - 1; i++) {
			Image currImg = images.get(i);
			Image nextImg = images.get(i + 1);

			if (exposureChanges.size() == 0) {
				startExpoChange = 0;
			} else {
				startExpoChange = exposureChanges.get(exposureChanges.size() - 1).getLastListNum() + 1;
			}

			// If the exposure is changed between current and next image
			if (isExposureChanged(currImg)) {

				ExposureChange newChange = new ExposureChange(images, startExpoChange, i);
				double exposure = 0;

				// If the shutter speed was changed
				if (currImg.getShutterSpeed() != nextImg.getShutterSpeed()) {
					// Use log and doubling function to calculate stops
					if (nextImg.getShutterNum() > currImg.getShutterNum()) {
						exposure += (Math.log(nextImg.getShutterNum() / currImg.getShutterNum())) / (Math.log(2));
					} else {
						exposure -= (Math.log(currImg.getShutterNum() / nextImg.getShutterNum())) / (Math.log(2));
					}
				}

				// If the aperture was changed
				if (currImg.getAperture() != nextImg.getAperture()) {
					// Use log function to calculate stops
					exposure += (Math.log(currImg.getAperture() / nextImg.getAperture())) / (Math.log(Math.sqrt(2)));
				}

				// If iso was changed
				if (currImg.getISO() != nextImg.getISO()) {
					// Use log function to calculate stops
					if (nextImg.getISO() > currImg.getISO()) {
						exposure += (Math.log(nextImg.getISO() / currImg.getISO())) / (Math.log(2));
					} else {
						exposure -= (Math.log(currImg.getISO() / nextImg.getISO())) / (Math.log(2));
					}
				}

				// If the exposure setting in Photoshop is changed..
				if (currImg.getExposureOffset() != nextImg.getExposureOffset()) {
					exposure += (nextImg.getExposureOffset() - currImg.getExposureOffset());
				}

				newChange.setExpoChange(exposure);

				exposureChanges.add(newChange);
			}

		}
	}

	public void analyzeWB() {
		int startWBChange = 0;

		for (int i = 0; i < images.size() - 1; i++) {
			Image currImg = images.get(i);
			Image nextImg = images.get(i + 1);

			if (temperatureChanges.size() == 0) {
				startWBChange = 0;
			} else {
				startWBChange = temperatureChanges.get(temperatureChanges.size() - 1).getLastListNum() + 1;
			}

			if (isWhiteBalanceChanged(currImg)) {
				TemperatureChange tempChange = new TemperatureChange(images, startWBChange, i);
				tempChange.setTemperatureChange(nextImg.getWhiteBalance() - currImg.getWhiteBalance());

				temperatureChanges.add(tempChange);
			}
		}
	}

	public boolean isExposureChanged(Image image) {
		Image next = images.get(image.getListNum() + 1);

		if (image.getISO() != next.getISO() | !image.getShutterSpeed().equals(next.getShutterSpeed())
				| image.getAperture() != next.getAperture() | image.getExposureOffset() != next.getExposureOffset()) {
			return true;
		}
		return false;
	}

	public boolean isWhiteBalanceChanged(Image image) {
		Image next = images.get(image.getListNum() + 1);

		// Tolerance of 100
		if (Math.abs(image.getWhiteBalance() - next.getWhiteBalance()) >= 100) {
			return true;
		}
		return false;
	}

	public void updateMetadata() {
		for (int i = 0; i < exposureChanges.size(); i++) {
			exposureChanges.get(i).updateMetadata("Exposure2012");
		}

		for (int i = 0; i < temperatureChanges.size(); i++) {
			temperatureChanges.get(i).updateMetadata("Temperature");
		}
	}

	/*
	 * public void overrideFiles() { double exposureValue = 100; double
	 * exposureChange;
	 * 
	 * for (int j = 0; j < exposureChanges.size(); j++) { for (int i = 0; i <
	 * exposureChanges.get(j).getImages(); i++) { Image currImg = images.get(i);
	 * double startExposure = 0;
	 * 
	 * if (i == 0) { startExposure = getExposureFromImage(currImg) / 10;
	 * exposureValue = startExposure * 10; exposureChange =
	 * (exposureChanges.get(j).getRatioChange() - startExposure) * 10;
	 * exposureChanges.get(j).setRatioChange(exposureChange);
	 * exposureChanges.get(j).setIncrements(); }
	 * 
	 * exposureValue += exposureChanges.get(j).getIncrements();
	 * System.out.println(exposureValue); } } }
	 */

	public void printChanges() {
		for (int i = 0; i < exposureChanges.size(); i++) {
			System.out.println("======== EXPOSURE CHANGE " + i + " ========");
			System.out.print(NumToName(exposureChanges.get(i).getStartListNum()) + " - "
					+ NumToName(exposureChanges.get(i).getLastListNum()));
			System.out.print(" (" + exposureChanges.get(i).getStartListNum() + " - "
					+ exposureChanges.get(i).getLastListNum() + ")");
			System.out.println(" [" + exposureChanges.get(i).getTotalImages() + " images]");
			System.out.print("(" + exposureChanges.get(i).getExpoChange() + ")");
			System.out.println(" [" + exposureChanges.get(i).getIncrements() + "]");
		}
		System.out.println();

		for (int i = 0; i < temperatureChanges.size(); i++) {
			System.out.println("======== WHITE BALANCE CHANGE " + i + " ========");
			System.out.print(NumToName(temperatureChanges.get(i).getStartListNum()) + " - "
					+ NumToName(temperatureChanges.get(i).getLastListNum()));
			System.out.print(" (" + temperatureChanges.get(i).getStartListNum() + " - "
					+ temperatureChanges.get(i).getLastListNum() + ")");
			System.out.println(" [" + temperatureChanges.get(i).getTotalImages() + " images]");
			System.out.print("(" + temperatureChanges.get(i).getTemperatureChange() + ")");
			System.out.println(" [" + temperatureChanges.get(i).getIncrements() + "]");
		}
	}

	public String NumToName(int num) {
		for (int i = 0; i < images.size(); i++) {
			if (i == num) {
				return images.get(i).getName();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		new proRAW();
	}
}