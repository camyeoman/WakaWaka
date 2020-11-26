package ghost;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Configuration {

	// Mode control
	List<Integer> modeLengths;
	int frightenedDuration;

	// Error
	final Errors error;

	// Map file name
	String mapFile;

	// Game attributes
	Sprite[][] spriteMap;
	int lives;
	int speed;

	enum Errors {
		fileNotFound,
		parseException;
	}

	public Configuration(String configFile) {
		Errors tempError = parseConfig(configFile);
		error = (tempError == null) ? parseMap() : tempError;
	}

	public Configuration() {
		error = null;
	}

	// Helper functions

	private static String[] extractMatches(String regex, String rawString)
	{
		// returns all the matches of a regular expression
		Matcher match = Pattern.compile(regex).matcher(rawString);
		List<String> matches = new ArrayList<>();
		while (match.find()) matches.add(match.group());

		return matches.toArray(new String[matches.size()]);
	}

	// Parsing functions

	public Errors parseConfig(String configFileName)
	{
		JSONObject config = null;

		try {
			Object file = new JSONParser().parse(new FileReader(configFileName)); 
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

	public Errors parseMap()
	{
		// string map
		spriteMap = new Sprite[36][28];

		try {
			File f = new File(mapFile);
			Scanner fileReader = new Scanner(f);

			for (int i=0; fileReader.hasNextLine(); i++) {
				String[] line = extractMatches("[0-9paciw]",fileReader.nextLine());
				spriteMap[i] = Arrays.stream(line)
					.map(c -> Sprite.getSprite(c))
					.toArray(Sprite[]::new);
			}
		} catch (FileNotFoundException e) {
			return Errors.fileNotFound;
		}

		return null;
	}
}
