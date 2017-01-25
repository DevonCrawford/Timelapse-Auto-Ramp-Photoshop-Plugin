import java.util.ArrayList;

public class TemperatureChange extends Change {

	private int tempChange;
	
	public TemperatureChange(ArrayList<Image> imgs, int sln, int pln) {
		super(imgs, sln, pln);
		increments = tempChange / totalImages;
	}
	
	public int getTemperatureChange() {
		return tempChange;
	}
	
	public void setTemperatureChange(int tc) {
		tempChange = tc;
		increments = tempChange / totalImages;
	}

}
