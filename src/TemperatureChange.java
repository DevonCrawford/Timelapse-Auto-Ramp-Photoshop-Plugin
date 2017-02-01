import java.util.ArrayList;

/* This class inherits Change to store data when a temperature (color)
 * change is detected between images. This class adds temperature data
 * to the Change class. 
 * 
 * by Devon Crawford
 */

public class TemperatureChange extends Change {
	private int tempChange;
	
	// Initializes change data
	public TemperatureChange(ArrayList<Image> imgs, int sln, int pln) {
		super(imgs, sln, pln);
		increments = tempChange / totalImages;
	}
	
	// Gets temperature change data
	public int getTemperatureChange() {
		return tempChange;
	}
	
	// Sets temperature change data
	public void setTemperatureChange(int tc) {
		tempChange = tc;
		increments = tempChange / totalImages;
	}

}
