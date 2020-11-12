package ghost;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.lang.Thread;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

class Data {
	String[][] stringMap;
	boolean[][] boolMap;
	PImage[][] imageMap;

	// App attributes
	String fileName;
	int lives;
	int speed;
	App app;
	List<Integer> modeLengths;


	// Sprites
	PImage frightenedGhost, playerClosed;
	Map<Direction, PImage> playerSprites;
	Map<Ghost.Type, PImage> ghostSprites;
	Map<GameObject.Type, PImage> gameSprites;
	Map<String, PImage> wallSprites;

	public Data(App app) {
		this.app = app;

		parseConfig();
		loadSprites();
		parseMap();
	}

	public void parseConfig()
	{
		// Read config file
		JSONObject config = null;
		try {
			Object file = new JSONParser().parse(new FileReader("config.json")); 
			config = (JSONObject) file;
		} catch (Exception e) {}

		// Name of map file
		fileName = (String) config.get("map"); 

		// Mode lengths
		String arrayString = ((JSONArray) config.get("modeLengths")).toString();
		String regex = "(?<=[\\[\\] ,])\\d+(?![0-9.])";
		String[] strArr = Utilities.extractMatches(regex, arrayString);
		modeLengths = new ArrayList<>();

		for (int i=0; i < strArr.length; i++) {
			int sum = 0;
			for (int j=0; j <= i; j++) {
				sum += 60 * Integer.parseInt(strArr[j]);
			}
			modeLengths.add(sum);
		}

		// Speed
		speed = Integer.parseInt(config.get("speed").toString());

		// Lives
		lives = Integer.parseInt(config.get("lives").toString());

		/*
			catch (FileNotFoundException e) {
			} catch (IOException e) {
			} catch (ParseException e) {
			}
		*/
	}

	public void loadSprites()
	{
		playerSprites = new HashMap<>();
		ghostSprites = new HashMap<>();
		wallSprites = new HashMap<>();
		gameSprites = new HashMap<>();

		// Create a PImage representation of map
		wallSprites.put("1", pathLoad(app, "horizontal"));
		wallSprites.put("2", pathLoad(app, "vertical"));
		wallSprites.put("3", pathLoad(app, "upLeft"));
		wallSprites.put("4", pathLoad(app, "upRight"));
		wallSprites.put("5", pathLoad(app, "downLeft"));
		wallSprites.put("6", pathLoad(app, "downRight"));

		playerSprites.put(Direction.right, pathLoad(app, "playerRight"));
		playerSprites.put(Direction.left, pathLoad(app, "playerLeft"));
		playerSprites.put(Direction.down, pathLoad(app, "playerDown"));
		playerSprites.put(Direction.up, pathLoad(app, "playerUp"));

		ghostSprites.put(Ghost.Type.ambusher, pathLoad(app, "ambusher"));
		ghostSprites.put(Ghost.Type.chaser, pathLoad(app, "chaser"));
		ghostSprites.put(Ghost.Type.ignorant, pathLoad(app, "ignorant"));
		ghostSprites.put(Ghost.Type.whim, pathLoad(app, "whim"));

		gameSprites.put(GameObject.Type.fruit, pathLoad(app,"fruit"));
		frightenedGhost = pathLoad(app, "frightened");
		playerClosed = pathLoad(app, "playerClosed");
	}

	public void parseMap()
	{
		// string map
		stringMap = new String[36][28];

		try {
			File f = new File(fileName);
			Scanner fileReader = new Scanner(f);
			String[][] map = new String[36][28];
			for (int i=0; fileReader.hasNextLine(); i++) {
				String[] line = Utilities.extractMatches("[0-7paciw]",fileReader.nextLine());

				if (i > 36 || line.length != 28) {
					return;
				} else {
					stringMap[i] = line;
				}
			}

			stringMap = stringMap;
		} catch (FileNotFoundException e) {
			return;
		}

		// boolean map
		boolMap = new boolean[36][28];

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				boolMap[j][i] = Pattern.matches("[paciw7]", stringMap[j][i]);
			}
		}

		// PImage map
		imageMap = new PImage[36][28];

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (stringMap[j][i] != null) {
					if (Pattern.matches("[1-6]", stringMap[j][i])) {
						imageMap[j][i] = wallSprites.get(stringMap[j][i]);
					}
				}
			}
		}
	}

	public PImage pathLoad(PApplet app, String str)
	{
		return app.loadImage("src/main/resources/" + str + ".png");
	}
}

public class Game {

	// Configuration settings
	List<Integer> modeLengths;
	Integer lives, speed;
	final int initLives;

	// Objects
	List<GameObject> gameObjects;
	List<Ghost> ghosts;
	Player player;

	// Map
	String[][] stringMap;
	PImage[][] imageMap;

	// Other
	int counter = 0;
	int points = 0;

	public Game(App app)
	{
		gameObjects = new ArrayList<>();
		ghosts = new ArrayList<>();

		Data data = new Data(app);

		initLives = data.lives;
		lives = data.lives;
		imageMap = data.imageMap;
		stringMap = data.stringMap;

		// setup classes
		Ghost.setUp(player, data.modeLengths, data.ghostSprites, data.frightenedGhost);
		Player.setUp(data.playerSprites, data.playerClosed);
		Agent.setUp(data.boolMap, data.speed);
		GameObject.setUp(data.gameSprites);

		createObjects(true);

		// load font
		app.textFont(
			app.createFont("src/main/resources/PressStart2P-Regular.ttf",
				20, false, new char[]{ 'G','a','m','e','O','v','e','r',' ' }
			)
		);
	}

	public void createObjects(boolean fruit)
	{
		gameObjects = (fruit) ? new ArrayList<>() : gameObjects;

		ghosts = new ArrayList<>();

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {

				if (stringMap[j][i].equals("p")) {
					player = new Player(16 * i, 16 * j);
				} else if (Pattern.matches("[aciw]", stringMap[j][i])) {
					ghosts.add(new Ghost(16 * i, 16 * j, stringMap[j][i].charAt(0)));
				} else if (stringMap[j][i].equals("7") && fruit) {
					gameObjects.add(new GameObject(GameObject.Type.fruit, 16 * i, 16 * j));
				}

			}
		}

		Ghost.Navigate.TARGET = player;
	}

	// Active methods

	public void drawMap(App app)
	{
		app.background(0,0,0);

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (imageMap[j][i] != null) {
					app.image(imageMap[j][i], 16 * i, 16 * j);
				}
			}
		}

		// draw scoreboard
		for (int i=0; i < lives; i++) {
			app.image(player.staticSprite(), 16 + 32 * i, 543);
		}
	}

	public void endScreen(App app, boolean won)
	{
		app.background(0,0,0);

		if (won) {
			app.text("You won", 154, 288);
		} else {
			app.text("Game Over", 134, 288);
		}

	}

	public void drawGameObjects(App app)
	{
		for (int i=0; i < gameObjects.size(); i++) {
			GameObject obj = gameObjects.get(i);
			obj.draw(app);
			if (obj.getPoint().distance(player.getPoint()) < 1) {
				points++;
				gameObjects.remove(i--);
			}
		}

		if (gameObjects.size() == 0) {
			endScreen(app, true);
		}
	}

	public void drawGhosts(App app)
	{
		for (int i=0; i < ghosts.size(); i++) {
			Ghost ghost = ghosts.get(i);
			ghost.draw(app, counter);

			if (ghost.tic(player, counter)) {
				counter = 0;
				lives--;
				if (lives > 0) {
					createObjects(false);
				} else {
					endScreen(app, false);
					return;
				}
			}

		}
	}

	public void tic(App app)
	{
		if (lives > 0) {
			refreshMovementCache(app);
			drawMap(app);
			drawGameObjects(app);
			player.tic(app, counter);
			drawGhosts(app);
			
			counter++;
		}
	}

	public boolean refreshMovementCache(App app)
	{
		Direction direct = player.getDirection();
		Integer currentDirection = (direct == null) ? null : direct.KEY_CODE;

		if (app.keyCode == 32) {
			app.debugMode = (app.debugMode) ? false : true;
			app.keyCode = 0;
		} else if ((currentDirection == null || app.keyCode != currentDirection)
				&& (app.keyCode <= 40 && app.keyCode >= 37)) {
			switch (app.keyCode) {
				case 37:
					player.setQuedDirection(Direction.left);
					break;
				case 38:
					player.setQuedDirection(Direction.up);
					break;
				case 39:
					player.setQuedDirection(Direction.right);
					break;
				case 40:
					player.setQuedDirection(Direction.down);
					break;
			}

			app.keyCode = 0;
			return true;
		}

		return false;
	}
}
