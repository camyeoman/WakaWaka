package ghost;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject; 
import org.json.simple.JSONArray; 
import org.json.simple.parser.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.io.File;

/**
 * An class used to parse user files and then transfer the parsed data to the
 * other classes in setup.
 */
public class Configuration {

	/** Time in seconds for each mode */
	List<Integer> modeLengths;

	/** Duration of the frightened duration found in config file */
	int frightenedDuration;

	/** Store any errors that occur */
	final Errors error;

	/** File path to map text file found in config file */
	String mapFile;

	/** Store the game map as a 2D array of Sprite objects.  */
	Sprite[][] spriteMap;

	/** Number of lives for player found in config file */
	int lives;

	/** Speed of all moving game entities found in config file */
	int speed;

	/** For storing the corners of the map */
	Map<String, Point> corners;

	/** For returning specific errors */
	enum Errors {
		fileNotFound,
		parseException;
	}

	/**
	 * Parse a config json file, and then if successful parse the map found in
	 * the config file. Use this constructor to read a configuration from a file.
	 * @param configFile, the relative file path of the config file
	 */
	public Configuration(String configFile) {
		Errors tempError = parseConfig(configFile);
		error = (tempError == null) ? parseMap() : tempError;
	}

	/**
	 * Empty constructor, allows the user to populate the config object manually.
	 * This is used for testing when a specific attribute is created manually for
	 * testing and not put into a test file.
	 */
	public Configuration() {
		// Error is final variable, must be initialised
		error = null;
	}


	// Helper functions

	/**
	 * Returns all matches of a regular expression in a given string as an array
	 * of strings. This is a quality of life function, not vital to program.
	 * @param regex, the regular expression string
	 * @param rawString, the string to be parsed
	 * @return all matches of the regular expression found in the string
	 */
	private static String[] extractMatches(String regex, String rawString)
	{
		// returns all the matches of a regular expression
		Matcher match = Pattern.compile(regex).matcher(rawString);
		List<String> matches = new ArrayList<>();
		while (match.find()) matches.add(match.group());

		return matches.toArray(new String[matches.size()]);
	}

	// Parsing functions

	/**
	 * Parse a json file and store the attributes found in the current instance
	 * of the Configuration object. If there is an error store that error in the
	 * error variable.
	 * @param configFilePath, the relative file path of the config file
	 * @return the error that occured, or null if none occured
	 */
	public Errors parseConfig(String configFilePath)
	{
		JSONObject config = null;

		try {
			Object file = new JSONParser().parse(new FileReader(configFilePath)); 
			config = (JSONObject) file;
		} catch (FileNotFoundException e) {
			return Errors.fileNotFound;
		} catch (ParseException e) {
			return Errors.parseException;
		} catch (IOException e) {}

		// Name of map file
		this.mapFile = (String) config.get("map"); 

		// frightened duration
		this.frightenedDuration = Integer.parseInt(
			config.get("frightenedLength").toString()
		);

		// Mode lengths
		String arrayString = ((JSONArray) config.get("modeLengths")).toString();
		String regex = "(?<=[\\[\\] ,])\\d+(?![0-9.])";
		String[] strArr = extractMatches(regex, arrayString);

		modeLengths = new ArrayList<>();
		for (int i=0; i < strArr.length; i++) {
			modeLengths.add(Integer.parseInt(strArr[i]));
		}

		// Speed
		speed = Integer.parseInt(config.get("speed").toString());

		// Lives
		lives = Integer.parseInt(config.get("lives").toString());

		return null;
	}

	/**
	* Parses and then stores the map, if found with no errors, and stores the
	* corners. Uses the mapFile attribute to read the map file and then if any
	* errors occur return them.
	* @return the error that occured, or null if none occured
	*/
	public Errors parseMap()
	{
		spriteMap = new Sprite[36][28];

		// Parse map file, if it exists

		try {
			File f = new File(mapFile);
			Scanner fileReader = new Scanner(f);

			for (int i=0; fileReader.hasNextLine(); i++) {
				// Note that valid characters are 0,1,2,3,4,5,6,7,8,9,p,a,c,i,w
				String[] line = extractMatches("[0-9paciw]",fileReader.nextLine());
				spriteMap[i] = Arrays.stream(line)
					.map(c -> Sprite.getSprite(c))
					.toArray(Sprite[]::new);
			}
		} catch (FileNotFoundException e) {
			return Errors.fileNotFound;
		}

		// Get corners of map

		int width  = 16 * spriteMap[0].length;
		int height = 16 * spriteMap.length;

		corners = new HashMap<String, Point>();

		corners.put("botRight", new Point(width, height));
		corners.put("botLeft" , new Point(0, height));
		corners.put("topRight", new Point(width, 0));
		corners.put("topLeft" , new Point(0, 0));

		return null;
	}
}
