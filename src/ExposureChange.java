import java.util.ArrayList;

public class ExposureChange extends Change {
	
	private double expoChange;

	public ExposureChange(ArrayList<Image> imgs, int sln, int pln) {
		super(imgs, sln, pln);

		increments = expoChange / totalImages;
	}
	
	public double getExpoChange() {
		return expoChange;
	}
	
	public void setExpoChange(double rc) {
		expoChange = rc;
		increments = expoChange / totalImages;
	}
	
	public void setIncrements() {
		increments = expoChange/totalImages;
	}

}
