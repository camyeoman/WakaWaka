package ghost;

import java.util.regex.*;
import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;


public class Utilities {

	// SETUP FUNCTIONS

	// Create a boolean representation of map
	public static boolean[][] booleanMap(String fileName)
	{
		String[][] stringMap = parseFile(fileName);
		boolean[][] boolMap = new boolean[36][28];

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				boolMap[j][i] = Pattern.matches("[paciw7]", stringMap[j][i]);
			}
		}

		return boolMap;
	}

	public static PImage pathLoad(PApplet app, String str)
	{
		return app.loadImage("src/main/resources/" + str + ".png");
	}

	public static void loadSprites(App app)
	{
		Player.loadSprites(app);
		Ghost.loadSprites(app);
		GameObject.loadSprites(app);
	}

	public static String parseConfig(App app)
	{
		// Read config file
		JSONObject config = null;
		try {
			Object file = new JSONParser().parse(new FileReader("config.json")); 
			config = (JSONObject) file;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (ParseException e) {
		}

		// Populate app with config data

		// Name of map file
		String fileName = (String) config.get("map"); 

		// Mode lengths
		String arrayString = ((JSONArray) config.get("modeLengths")).toString();
		String[] strArr = Utilities.extractMatches("(?<=[\\[\\] ,])\\d+(?![0-9.])", arrayString);
		app.modeLengths = new int[strArr.length];
		for (int i=0; i < strArr.length; i++) {
			app.modeLengths[i] = Integer.parseInt(strArr[i]);
		}

		// Speed
		Agent.setSpeed(Integer.parseInt(config.get("speed").toString()));

		// Lives
		app.lives = Integer.parseInt(config.get("lives").toString());

		return fileName;
	}

	public static void populateClasses(App app, String fileName)
	{
		// Create a PImage representation of map
		String[] files = new String[]{
			"horizontal","vertical","upLeft",
			"upRight","downLeft","downRight"
		};
		Map<String, PImage> wallSprites = new HashMap<>();
		for (int i=1; i <= 6; i++) {
			wallSprites.put("" + i, pathLoad(app,files[i-1]));
		}

		String[][] stringMap = parseFile(fileName);
		app.map = new PImage[36][28];

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {

				if (stringMap[j][i] != null) {
					if (Pattern.matches("[1-6]", stringMap[j][i])) {
						app.map[j][i] = wallSprites.get(stringMap[j][i]);
					}
				}

			}
		}

		// set the boolMap variable for Agent
		Agent.boolMap = booleanMap(fileName);

		// Popluate App instance with the lists and variables
		// holding the relavent objects
		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {

				if (stringMap[j][i].equals("p")) {
					app.player = new Player(16 * i, 16 * j);
				} else if (Pattern.matches("[aciw]", stringMap[j][i])) {
					app.ghosts.add(new Ghost(16 * i, 16 * j, stringMap[j][i].charAt(0)));
				} else if (stringMap[j][i].equals("7")) {
					app.gameObjects.add(new GameObject(GameObject.Types.fruit, 16 * i, 16 * j));
				}

			}
		}
	}

	// Parse string representation of map from file.
	public static String[][] parseFile(String fileName)
	{
		try {
			File f = new File(fileName);
			Scanner fileReader = new Scanner(f);
			String[][] map = new String[36][28];
			for (int i=0; fileReader.hasNextLine(); i++) {
				String[] line = Utilities.extractMatches("[0-7paciw]",fileReader.nextLine());

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

	public static double distance(int[] a, int[] b)
	{
		int xDist = (int)Math.pow(a[0] - b[0], 2);
		int yDist = (int)Math.pow(a[1] - b[1], 2);
		return (int)Math.pow(xDist + yDist, 0.5);
	}

	public static double round(double n, int places)
	{
		int pow10 = (int)(Math.pow(10,places-1));
		return (double)Math.round(n * pow10) / pow10;
	}

	public static String[] extractMatches(String regex, String rawString)
	{
		// returns all the matches of a regular expression
		Matcher match = Pattern.compile(regex).matcher(rawString);
		List<String> matches = new ArrayList<>();
		while (match.find()) matches.add(match.group());

		return matches.toArray(new String[matches.size()]);
	}
}
