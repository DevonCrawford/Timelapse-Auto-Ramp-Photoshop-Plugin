import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Util {

	// Gets all data from a file, returns as one string
	public String getData(File file) {
		String data = "";
		try {
			BufferedReader fr = new BufferedReader(new FileReader(file.getAbsolutePath()));
			String line = fr.readLine();

			while (line != null) {
				data += line;
				line = fr.readLine();
			}
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	// Finds a value, given the xmp file, and a key (eg. "Exposure2012")
	// No worry about =". Automatically calculates length
	public String find(File file, String key) {
		String data = getData(file);
		String string = "";
		int startLocation = data.indexOf(key) + key.length() + 2;
		int increment = 0;
		char character = data.charAt(startLocation);

		while (character != '"') {
			string += character;
			increment++;
			if ((startLocation + increment) >= data.length()) {
				break;
			}
			character = data.charAt(startLocation + increment);
		}
		return string;
	}

	public String replace(File file, String key, double replace) {
		// Gets file data in string
		String data = getData(file);

		// Gets location of key
		int startLocation = data.indexOf(key) + key.length() + 2;
		
		int endLocation = startLocation;

		// Gets location of end of replace, rest of file
		while (data.charAt(endLocation) != ' ') {
			endLocation++;
		}

		// Saves up to the replace point in old data
		String newData = data.substring(0, startLocation);

		// Add replace value to new data
		newData += replace;

		// Finish formatting end of replacement
		newData += "\"" + data.substring(endLocation, data.length());

		return newData;
	}

	public String createValue(File file, String preKey, String replace, String value) {
		String data = getData(file);

		int startLocation = data.indexOf(preKey);

		// Gets location to start the replace
		while (data.charAt(startLocation) != ' ') {
			startLocation++;
		}

		// Saves up to this point in original file
		String newFile = data.substring(0, startLocation);
		newFile += "   " + replace + "\"";
		newFile += value + "\"";

		newFile += data.substring(startLocation, data.length());

		return newFile;
	}

	public void writeFile(Image image, String newData) {
		// Get image data file
		File newFile = new File(image.getDataPath());
		FileWriter writer;
		try {
			writer = new FileWriter(newFile, false);
			writer.write(newData);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // true to append
	}

	public void displayInfo(ArrayList<Image> images) {
		
		System.out.println("\n================ XMP Information ================");
		
		for(int i = 0; i < images.size(); i++) {
			Image image = images.get(i);
			System.out.println(image.getXMP().getName());
			System.out.println("Exposure2012: " + find(image.getXMP(), "Exposure2012"));
			System.out.println(image.getExposureOffset());
			System.out.println("Temperature: " + find(image.getXMP(), "Temperature"));
			System.out.println("--------------------------");
		}
	}

	public boolean isNumeric(String s) {
		return s.matches("[-+]?\\d*\\.?\\d+");
	}

}
