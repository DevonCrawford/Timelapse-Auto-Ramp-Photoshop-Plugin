import java.io.File;
import java.util.ArrayList;

public class Change {

	Util util = new Util();
	ArrayList<Image> images;
	protected double increments;
	protected int lastListNum, startListNum;
	protected int totalImages;
	
	public Change(ArrayList<Image> imgs, int sln, int pln) {
		images = imgs;
		startListNum = sln;
		lastListNum = pln;
		totalImages = (lastListNum - startListNum) + 1;
	}
	
	public int getStartListNum() {
		return startListNum;
	}
	
	public int getLastListNum() {
		return lastListNum;
	}
	
	public int getTotalImages() {
		return totalImages;
	}
	
	public double getIncrements() {
		return increments;
	}
	
	public void setStartListNum(int sln) {
		startListNum = sln;
	}
	
	public void setPrevListNum(int pln) {
		lastListNum = pln;
	}
	
	public void updateMetadata(String key) {
		// Gets start value of change
		double startValue = Double.parseDouble(util.find(images.get(startListNum).getXMP(), key));
		
		for(int i = startListNum; i <= lastListNum; i++) {
			Image currImg = images.get(i);
			
			// Gets the xmp file
			File file = currImg.getXMP();
			
			// Creates new value, incremented
			double newValue = startValue + (increments * (i-startListNum));
			
			// Replaces string data with new value
			String newData = util.replace(file, key, newValue);
			
			if(key.equals("Exposure")) {
				double exposure = Double.parseDouble(util.find(currImg.getXMP(), "Exposure2012"));
				currImg.setExposureOffset(exposure);
			}
			else if(key.equals("Temperature")) {
				int whiteBalance = Integer.parseInt(util.find(currImg.getXMP(), "Temperature"));
				currImg.setWhiteBalance(whiteBalance);
			}
			
			// Overwrites xmp file with new string data
			util.writeFile(currImg, newData);
		}
	}
}
