import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/* proRAW is the main class that controls all processing.
 * Retrieving data, analyzing exposure, white balance, 
 * and updating metadata. This class is responsible to
 * store its data in the other classes, and mathematically
 * calculate proper offsets to match exposure and temperature
 * changes in a sequence of images (timelapse)
 * 
 * by Devon Crawford
 */

public class proRAW {
	Util util;
	ArrayList<Image> images;
	ArrayList<ExposureChange> exposureChanges;
	ArrayList<TemperatureChange> temperatureChanges;
	double exposureIncrements;
	int startLocation, endLocation;

	// Initializing lists and calling main functions
	public proRAW() {
		util = new Util();
		images = new ArrayList<Image>();
		exposureChanges = new ArrayList<ExposureChange>();
		temperatureChanges = new ArrayList<TemperatureChange>();
		
		// Outputs welcome text
		welcome();
		
		// Gets user input for start image path
		Scanner sc = new Scanner(System.in);
		String path = sc.nextLine();
		
		// Creates start image file object
		File start = new File(path);

		// Processes algorithm if data is retrieved without error
		if(retrieveData(start.getParent(), start.getName())) {
			analyzeExposure();
			analyzeWB();
			printChanges();
			
			System.out.println();
			System.out.println("Saving to files..");
			updateMetadata();
			
			// Displays information from .xmp files
//			util.displayInfo(images);
			
			// Outputs finish text
			finish();
		}
	}

	// Retrieves data from all images
	public boolean retrieveData(String folder, String startName) {
		// Starting file
		File img = new File(folder + "\\" + startName);
		File xmp = new File(folder + "\\" + util.getXMPName(startName));
		
		// If start file does not exist
		if(!img.exists()) {
			noImgErr(img);
			return false;
		}

		// Iterates through every file in sequence
		while (img.exists() && xmp.exists()) {
			Image newImage = new Image(img.getName(), folder);
			newImage.setListNum(images.size());
			getImageData(newImage);
			images.add(newImage);

//			newImage.printInfo();
			System.out.println("Loaded: " + newImage.getFilePath());

			// gets file of next image (number + 1)
			img = getNextImage(folder, newImage);
			xmp = new File(folder + "\\" + util.getXMPName(img.getName()));
		}
		System.out.println();
		
		// If we did not get through all files that exist in directory.. files must be missing
		if((images.size() * 2) < new File(folder).listFiles().length) {
			// Output error messages for missing files
			if(!img.exists()) {
				noImgErr(img);
			}
			else if(!xmp.exists()) {
				System.out.println("Error: \"" + xmp.getAbsolutePath() + "\" does not exist.");
				System.out.println("Be sure to generate .xmp files from photoshop. LOOKUP: save metadata to files");
			}
		}
		return true;
	}

	/* Returns next image file by finding the image number,
	 * incrementing it, and placing the file path back together */
	public File getNextImage(String folder, Image currIMG) {
		String imageName = currIMG.getName();

		String preNum = "";
		String stringNum = "";
		String extension = "";

		// Grabs the image number and extension separately
		for (int i = 0; i < imageName.length(); i++) {
			// Check for extension characters first
			if (imageName.charAt(i) == '.') {
				// Grab characters of extension, then exit parsing
				for (int j = i; j < imageName.length(); j++) {
					extension += imageName.charAt(j);
				}
				break;
			}
			// Check if character is a digit
			else if (Character.isDigit(imageName.charAt(i))) {
				stringNum += imageName.charAt(i);
			} 
			// Else, character must be text before the number
			else {
				preNum += imageName.charAt(i);
			}
		}
		// Increments image number. Uses it to get next file name
		int nextNum = Integer.parseInt(stringNum) + 1;
		return new File(folder + "\\" + preNum + nextNum + extension);
	}

	public Image getPrevImage(Image currIMG) {
		return images.get(currIMG.getListNum() - 1);
	}

	// Retrieves important image data from file
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

		// Finds shutter speed value in file as a String
		String shutterSpeed = util.find(xmp, "ExposureTime");

		// Finds aperture value in file as a String
		String apString = util.find(xmp, "FNumber");
			
		// Saves parts of the aperture fractions individually
		String leftA = apString.substring(0, apString.indexOf('/'));
		String rightA = apString.substring(leftA.length() + 1, apString.length());

		// Divides fraction into double aperture value
		double aperture = Double.parseDouble(leftA) / Double.parseDouble(rightA);

		// Finds iso value in file as a String
		String iso = util.find(xmp, "RecommendedExposureIndex");

		// Finds exposureOffset (photoshop adjustment) in file as a String
		String exposureOffset = util.find(xmp, "Exposure2012");
			
		// Sets calculated data on image object
		image.setShutterSpeed(shutterSpeed);
		image.calcShutterNum();
		image.setAperture(aperture);
		image.setISO(Integer.parseInt(iso));
		image.setExposureOffset(Double.parseDouble(exposureOffset));
	}

	/* Analyzes exposure, searching for changes and creating "exposureChanges"
	 * lists, to store images data in change sequence. Then Calculate the
	 * exposure offset required to match differences in images.	*/
	public void analyzeExposure() {
		int startExpoChange = 0;

		// Iterates through each image
		for (int i = 0; i < images.size() - 1; i++) {
			Image currImg = images.get(i);
			Image nextImg = images.get(i + 1);

			// Sets start image of exposure change
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

				// Set calculated change to object
				newChange.setExpoChange(exposure);

				// Save exposure change to exposureChanges list!
				// Allowing multiple exposure changes to occur and be analyzed independently
				exposureChanges.add(newChange);
			}

		}
	}

	/* Analyzes white balance, searching for changes and creating
	 * "temperatureChanges" lists, to store change data in sequence. 
	 * Then calculate the temperature offset required to match
	 * differences in images.	*/
	public void analyzeWB() {
		int startWBChange = 0;

		// Iterates through all images
		for (int i = 0; i < images.size() - 1; i++) {
			Image currImg = images.get(i);
			Image nextImg = images.get(i + 1);

			// Sets start image of temperature change list
			if (temperatureChanges.size() == 0) {
				startWBChange = 0;
			} else {
				startWBChange = temperatureChanges.get(temperatureChanges.size() - 1).getLastListNum() + 1;
			}

			// If white balance is changed
			if (isWhiteBalanceChanged(currImg)) {
				// Create new temperature change, calculate difference, and set change
				TemperatureChange tempChange = new TemperatureChange(images, startWBChange, i);
				tempChange.setTemperatureChange(nextImg.getWhiteBalance() - currImg.getWhiteBalance());

				// Add temperature change to "temperatureChanges" list
				temperatureChanges.add(tempChange);
			}
		}
	}

	/* Returns true if exposure setting on camera was
	 * changed between "image" and next image.	*/
	public boolean isExposureChanged(Image image) {
		Image next = images.get(image.getListNum() + 1);

		if (image.getISO() != next.getISO() | !image.getShutterSpeed().equals(next.getShutterSpeed())
				| image.getAperture() != next.getAperture() | image.getExposureOffset() != next.getExposureOffset()) {
			return true;
		}
		return false;
	}

	/* Returns true is white balance was changed
	 * between "image and next image.	*/
	public boolean isWhiteBalanceChanged(Image image) {
		Image next = images.get(image.getListNum() + 1);

		// Tolerance of 100
		if (Math.abs(image.getWhiteBalance() - next.getWhiteBalance()) >= 100) {
			return true;
		}
		return false;
	}

	// Updates metadata of every change, on every image
	public void updateMetadata() {
		for (int i = 0; i < exposureChanges.size(); i++) {
			exposureChanges.get(i).updateMetadata("Exposure2012");
		}

		for (int i = 0; i < temperatureChanges.size(); i++) {
			temperatureChanges.get(i).updateMetadata("Temperature");
		}
	}

	// Prints all change information from all images
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
	
	public void welcome() {
		System.out.println("========== Welcome to proRAW v1.0 ========== 	by Devon Crawford");
		System.out.println();
		System.out.println("This program will smoothly ramp the exposure");
		System.out.println("and white balance throughout a timelapse.");
		System.out.println();
		System.out.println("Please enter the filepath to the first image");
		System.out.println("in sequence (.CR2 file including extension)");
		System.out.println();
		System.out.print("Path: ");
	}
	
	public void finish() {
		System.out.println();
		System.out.println("Metadata updated!");
		System.out.println("You may now open these files in after effects or media encoder");
		System.out.println("to render the timelapse. You can also open the images in");
		System.out.println("photoshop or lightroom to view changes. I have not tested");
		System.out.println("in all of adobe software but I believe that this would work");
		System.out.println("in any of their programs. Enjoy!");
		System.out.println();
		System.out.println("- Devon Crawford");
	}
	
	public void noImgErr(File img) {
		System.out.println("Error: \"" + img.getAbsolutePath() + "\" does not exist.");
	}

	// Finds an image name from its list number
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