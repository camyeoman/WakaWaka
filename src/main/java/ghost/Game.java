package ghost;

import java.util.regex.Pattern;
import java.util.Arrays;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

class Configuration {
	Sprite[][] spriteMap;

	// App attributes
	String fileName;
	int lives;
	int speed;
	App app;
	List<Integer> modeLengths;


	public Configuration(App app)
	{
		this.app = app;

		parseConfig();
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

	public void parseConfig()
	{
		JSONObject config = null;
		try {
			Object file = new JSONParser().parse(new FileReader("config.json")); 
			config = (JSONObject) file;
		} catch (Exception e) {}
		/*
			catch (FileNotFoundException e) {
			} catch (IOException e) {
			} catch (ParseException e) {
			}
		*/


		// Name of map file
		fileName = (String) config.get("map"); 

		// Mode lengths
		String arrayString = ((JSONArray) config.get("modeLengths")).toString();
		String regex = "(?<=[\\[\\] ,])\\d+(?![0-9.])";
		String[] strArr = extractMatches(regex, arrayString);
		modeLengths = new ArrayList<>();

		for (int i=0; i < strArr.length; i++) {
			int sum = 0;
			for (int j=0; j <= i; j++) {
				sum += Integer.parseInt(strArr[j]);
			}
			modeLengths.add(sum);
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

public class Game {
	// Configuration settings
	List<Integer> modeLengths;
	int lives, speed;
	int initLives;

	// Objects
	List<GameObject> gameObjects;
	List<Ghost> ghosts;
	Player player;

	// Map
	Sprite[][] spriteMap;

	// Sprites
	Map<Sprite, PImage> allSprites;

	// Other
	App app;
	int counter = 0;
	int points = 0;

	public Game(App app)
	{
		this.app = app;
		gameObjects = new ArrayList<>();
		ghosts = new ArrayList<>();
	}

	private PImage pathLoad(String str)
	{
		return app.loadImage("src/main/resources/" + str + ".png");
	}

	public void setup()
	{
		Configuration data = new Configuration(app);

		allSprites = new HashMap<>();

		allSprites.put(Sprite.horizontal, pathLoad("horizontal"));
		allSprites.put(Sprite.vertical, pathLoad("vertical"));
		allSprites.put(Sprite.upLeft, pathLoad("upLeft"));
		allSprites.put(Sprite.upRight, pathLoad("upRight"));
		allSprites.put(Sprite.downLeft, pathLoad("downLeft"));
		allSprites.put(Sprite.downRight, pathLoad("downRight"));

		allSprites.put(Sprite.playerRight, pathLoad("playerRight"));
		allSprites.put(Sprite.playerLeft, pathLoad("playerLeft"));
		allSprites.put(Sprite.playerDown, pathLoad("playerDown"));
		allSprites.put(Sprite.playerUp, pathLoad("playerUp"));
		allSprites.put(Sprite.playerUp, pathLoad("playerClosed"));

		allSprites.put(Sprite.ghostFrightened, pathLoad("frightened"));
		allSprites.put(Sprite.ghostIgnorant, pathLoad("ignorant"));
		allSprites.put(Sprite.ghostAmbusher, pathLoad("ambusher"));
		allSprites.put(Sprite.ghostChaser, pathLoad("chaser"));
		allSprites.put(Sprite.ghostWhim, pathLoad("whim"));

		// TODO load Super fruit here!!
		allSprites.put(Sprite.fruit, pathLoad("fruit"));

		initLives = data.lives;
		lives = data.lives;
		spriteMap = data.spriteMap;

		createObjects(true);

		// setup classes
		Ghost.setup(player, data.modeLengths);
		Agent.setup(spriteMap, data.speed);

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

				if (spriteMap[j][i].equals("p")) {
					player = new Player(16 * i, 16 * j);
				} else if (spriteMap[j][i].isGhost()) {
					ghosts.add(new Ghost(16 * i, 16 * j, spriteMap[j][i]));
				} else if (spriteMap[j][i] == Sprite.fruit && fruit) {
					gameObjects.add(new GameObject(GameObject.Type.fruit, 16 * i, 16 * j));
				}

			}
		}

		Ghost.Type.PLAYER = player;
	}

	// Active methods

	public void drawMap(App app)
	{
		app.background(0,0,0);

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (spriteMap[j][i] != null) {
					app.image(allSprites.get(spriteMap[j][i]), 16 * i, 16 * j);
				}
			}
		}

		// draw scoreboard
		for (int i=0; i < lives; i++) {
			app.image(allSprites.get(player.staticSprite()), 16 + 32 * i, 543);
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
			obj.draw(this);
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
			ghost.draw(this, counter);

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
			player.tic(this, counter);
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
