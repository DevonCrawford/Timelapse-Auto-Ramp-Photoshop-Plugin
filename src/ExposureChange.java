import java.util.ArrayList;

/* This class inherits Change to store data of images when an
 * exposure change is detected between images. This class adds
 * exposure data to the Change class.
 * 
 * by Devon Crawford
 */

public class ExposureChange extends Change {
	
	private double expoChange;

	// Initializes change data
	public ExposureChange(ArrayList<Image> imgs, int sln, int pln) {
		super(imgs, sln, pln);

		increments = expoChange / totalImages;
	}
	
	// Gets exposure change data
	public double getExpoChange() {
		return expoChange;
	}
	
	// Sets the exposure change data
	public void setExpoChange(double rc) {
		expoChange = rc;
		increments = expoChange / totalImages;
	}
	
	// Sets increments across all images in change
	public void setIncrements() {
		increments = expoChange/totalImages;
	}

}
