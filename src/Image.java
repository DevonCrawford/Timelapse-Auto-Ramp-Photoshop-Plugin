import java.io.File;

public class Image {
	
	Util util = new Util();
	private String fileName, filePath, xmpPath, shutterSpeed;
	private int iso, listNum, whiteBalance;
	private double ratio, aperture, shutterNum, exposureOffset;
	
	public Image(String n) {
		fileName = n;
	}
	
	public Image(String n, String folder) {
		listNum = 0;
		fileName = n;
		filePath = folder + n;
		exposureOffset = 0;
		
		xmpPath = folder;
		
		// Gets xmp file name from image
		for (int k = 0; k < fileName.length(); k++) {
			if (fileName.charAt(k) != '.') {
				xmpPath += fileName.charAt(k);
			} else {
				xmpPath += ".xmp";
				break;
			}
		}
	}
	
	public Image(Util util) {
		this.util = util;
		fileName = "";
		ratio = 0;
		listNum = 0;
		exposureOffset = 0;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public File getFile() {
		return new File(filePath);
	}
	
	public File getXMP() {
		return new File(xmpPath);
	}
	
	public String getDataPath() {
		return xmpPath;
	}
	
	public int getListNum() {
		return listNum;
	}
	
	public String getShutterSpeed() {
		return shutterSpeed;
	}
	
	public void calcShutterNum() {
		String numS = shutterSpeed.substring(0, shutterSpeed.indexOf('/'));
		String denS = shutterSpeed.substring(numS.length() + 1, shutterSpeed.length());
		
		int num = Integer.parseInt(numS);
		int den = Integer.parseInt(denS);
		shutterNum = ((num + 0.00000000)/den);
	}
	
	public double getShutterNum() {
		return shutterNum;
	}
	
	public double getExposureOffset() {
		return exposureOffset;
	}
	
	public double getAperture() {
		return aperture;
	}
	
	public int getISO() {
		return iso;
	}
	
	public int getWhiteBalance() {
		return whiteBalance;
	}
	
	public String getName() {
		return fileName;
	}
	
	public double getRatio() {
		return ratio;
	}
	
	public void setListNum(int n) {
		listNum = n;
	}
	
	public void setName(String n) {
		fileName = n;
	}
	
	public void setShutterSpeed(String s) {
		shutterSpeed = s;
	}
	
	public void setAperture(double a) {
		aperture = a;
	}
	
	public void setISO(int i) {
		iso = i;
	}
	
	public void setWhiteBalance(int wb) {
		whiteBalance = wb;
	}
	
	public void setExposureOffset(double e) {
		exposureOffset = e;
	}
	
	public void printInfo() {
		System.out.println("File Name: " + fileName);
		System.out.println("File Path: " + filePath);
		System.out.println("XMP Path: " + xmpPath);
		System.out.println("Shutter Speed: " + shutterSpeed + " [" + shutterNum + "]");
		System.out.println("Aperture: " + aperture);
		System.out.println("ISO: " + iso);
		System.out.println("White Balance: " + whiteBalance);
		System.out.println("------------------------");
	}
}
