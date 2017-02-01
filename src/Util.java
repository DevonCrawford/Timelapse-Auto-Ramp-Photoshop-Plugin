import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/* This is my utilities class. It stores functions responsible for
 * reading/writing data from files, finding/replacing values, and
 * displaying relevant information.
 * 
 * by Devon Crawford
 */

public class Util {

	// Gets all data from a file, returns as one string
	public String getData(File file) {
		if(file.exists()) {
			String data = "";
			try {
				// Open file for reading
				BufferedReader fr = new BufferedReader(new FileReader(file.getAbsolutePath()));
				String line = fr.readLine();

				// Reads all lines of file, appending the lines to data string
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
		return null;
	}

	// Finds a value, given the xmp file, and a key (eg. "Exposure2012")
	// No worry about =". Automatically calculates length
	public String find(File file, String key) {
		String data = getData(file);
		String string = "";
		
		// Finds start index of value given the key
		int startLocation = data.indexOf(key) + key.length() + 2;
		int increment = 0;
		
		// Gets start character given the start index
		char character = data.charAt(startLocation);

		// Reads up until next quotation mark
		while (character != '"') {
			// Appends character to string result of data
			string += character;
			
			// Increments index for character
			increment++;
			
			// If location is past end of data, stop search
			if ((startLocation + increment) >= data.length()) {
				break;
			}
			// Get next character
			character = data.charAt(startLocation + increment);
		}
		return string;
	}

	// Replaces a value in file from its "key"
	// Example key: ("Exposure2012")
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

	// Creates a new value after the "preKey" value in file
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

	// Writes updated string data to file
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
	
	// Get xmp file name from image
	public String getXMPName(String n) {
		String xmpName = "";
		// Iterate each character of name,
		// append ".xmp" once '.' is found
		for (int k = 0; k < n.length(); k++) {
			if (n.charAt(k) != '.') {
				xmpName += n.charAt(k);
			} else {
				xmpName += ".xmp";
				break;
			}
		}
		return xmpName;
	}

	// Outputs data of all images in list
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
