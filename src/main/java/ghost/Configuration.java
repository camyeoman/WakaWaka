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
	Sprite[][] spriteMap;

	// Attributes
	List<Integer> modeLengths;
	String fileName;
	int lives;
	int speed;
	App app;

	public Configuration(String configFile)
	{
		parseConfig(configFile);
		parseMap();
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

	public void parseConfig(String configFileName)
	{
		JSONObject config = null;
		try {
			Object file = new JSONParser().parse(new FileReader(configFileName)); 
			config = (JSONObject) file;
		} catch (Exception e) {}
		/*
			catch (FileNotFoundException e) {
			} catch (IOException e) {
			} catch (ParseException e) {
			}
		*/


		// Name of map file
		this.fileName = (String) config.get("map"); 

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
	}

	public void parseMap()
	{
		// string map
		spriteMap = new Sprite[36][28];

		try {
			File f = new File(fileName);
			Scanner fileReader = new Scanner(f);

			for (int i=0; fileReader.hasNextLine(); i++) {
				String[] line = extractMatches("[0-7paciw]",fileReader.nextLine());

				if (i > 36 || line.length != 28) {
					// error
					return;
				} else {
					spriteMap[i] = Arrays.stream(line)
						.map(c -> Sprite.getSprite(c))
						.toArray(Sprite[]::new);
				}
			}
		} catch (FileNotFoundException e) {
			return;
		}
	}
}

