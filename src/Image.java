import java.io.File;

/* This class stores all data for one image. Each image is created
 * as an object with this class to organize the data easily. 
 * 
 * by Devon Crawford
 */

public class Image {
	
	Util util = new Util();
	private String fileName, filePath, xmpPath, shutterSpeed;
	private int iso, listNum, whiteBalance;
	private double ratio, aperture, shutterNum, exposureOffset;
	
	public Image(String n) {
		fileName = n;
	}
	
	// Initialize object
	public Image(String n, String folder) {
		listNum = 0;
		fileName = n;
		filePath = folder + "\\" + n;
		exposureOffset = 0;
		
		xmpPath = "";
		xmpPath += folder + "\\" + util.getXMPName(n);
	}
	
	// Initialize object
	public Image(Util util) {
		this.util = util;
		fileName = "";
		ratio = 0;
		listNum = 0;
		exposureOffset = 0;
	}
	
	// Returns string file path
	public String getFilePath() {
		return filePath;
	}
	
	// Returns File object of image file
	public File getFile() {
		return new File(filePath);
	}
	
	// Returns XMP file of image
	public File getXMP() {
		return new File(xmpPath);
	}
	
	// Returns path to XMP file
	public String getDataPath() {
		return xmpPath;
	}
	
	// Returns list number of image in sequence
	public int getListNum() {
		return listNum;
	}
	
	public String getShutterSpeed() {
		return shutterSpeed;
	}
	
	// Calculates double shutter value from fractional value
	public void calcShutterNum() {
		String numS = shutterSpeed.substring(0, shutterSpeed.indexOf('/'));
		String denS = shutterSpeed.substring(numS.length() + 1, shutterSpeed.length());
		
		int num = Integer.parseInt(numS);
		int den = Integer.parseInt(denS);
		shutterNum = ((num + 0.00000000)/den);
	}
	
	// Gets double shutter value
	public double getShutterNum() {
		return shutterNum;
	}
	
	// Gets exposure offset (processed value)
	public double getExposureOffset() {
		return exposureOffset;
	}
	
	public double getAperture() {
		return aperture;
	}
	
	public int getISO() {
		return iso;
	}
	
	// Gets white balance value
	public int getWhiteBalance() {
		return whiteBalance;
	}
	
	// Gets name of file
	public String getName() {
		return fileName;
	}
	
	public double getRatio() {
		return ratio;
	}
	
	// Sets location in image sequence
	public void setListNum(int n) {
		listNum = n;
	}
	
	// Sets name of file
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
	
	// Prints all information of image
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
