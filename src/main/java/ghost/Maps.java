package ghost;

import java.util.*;
import java.io.*;
import java.util.stream.*;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;

import processing.core.PImage;

public class Maps {

	public static String[][] parseFile(String fileName) {
		try {
			File f = new File(fileName);
			Scanner fileReader = new Scanner(f);
			String[][] map = new String[36][28];
			for (int i=0; fileReader.hasNextLine(); i++) {
				String[] line = Utilities.extractMatches("[0-7pg]",fileReader.nextLine());

				// Invalid board
				if (i > 36 || line.length != 28) {
					return null;
				} else {
					map[i] = line;
				}
			}

			return map;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static JSONObject parseConfig(String fileName) {
		try {
			Object file = new JSONParser().parse(new FileReader(fileName)); 
			JSONObject config = (JSONObject) file;
			return config;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}
	}

}
