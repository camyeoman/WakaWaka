package ghost;

import processing.core.PApplet;
import processing.core.PImage;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 

import java.util.*;
import java.util.regex.*;

interface lambda<T, U> {
	public U eval(T input);
}

public class App extends PApplet {
	public static final int HEIGHT = 576;
	public static final int WIDTH = 448;

	private int[] modeLengths;
	private String mapFile;
	private int lives;

	private Map<String, PImage> clearSprites = new HashMap<>();
	private List<GameObject> gameObjects = new ArrayList<>();
	private List<Ghost> ghosts = new ArrayList<>();
	private Player player = null;
	private PImage clearSquare = null;

	private PImage[][] map;

	private boolean debugMode = false;
	private int counter = -1;

	public App()
	{
		// Parse config
		JSONObject foo = Maps.parseConfig("config.json");
		mapFile = (String)foo.get("map"); 

		String arrayString = ((JSONArray) foo.get("modeLengths")).toString();
		String[] strArr = Utilities.extractMatches("\\d+", arrayString);
		int[] modeLengths = new int[strArr.length];
		for (int i=0; i < strArr.length; i++) {
			modeLengths[i] = Integer.parseInt(strArr[i]);
		}

		lives = Integer.parseInt(foo.get("lives").toString());
		Agent.setSpeed(Integer.parseInt(foo.get("speed").toString()));

		// Parse Map
		String[][] stringMap = Maps.parseFile(mapFile);

		boolean[][] boolMap = new boolean[36][28];
		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				boolMap[j][i] = Pattern.matches("[pg7]", stringMap[j][i]);

				if (stringMap[j][i].equals("p")) {
					this.player = new Player(i, j, boolMap);
				} else if (stringMap[j][i].equals("g")) {
					this.ghosts.add(new Ghost(i, j, boolMap));
				} else if (stringMap[j][i].equals("7")) {
					this.gameObjects.add(new GameObject(GameObject.Types.fruit, i, j));
				}
			}
		}

	}

	public void setup()
	{
		frameRate(60);

		// Load Sprites

		lambda<String, PImage> pathLoad
			= x -> { return this.loadImage("src/main/resources/" + x + ".png"); };

		// Load empyt square
		clearSquare = pathLoad.eval("blackSquare");

		Player.closed = pathLoad.eval("playerClosed");
		String[] files = new String[]{"Up", "Left", "Right", "Down"};
		for (int i=0; i < 4; i++) {
			Player.sprites.put(
					Direction.values()[i], pathLoad.eval("player" + files[i])
			);
		}

		// Load Player and ghost sprites
		Ghost.sprite = pathLoad.eval("ambusher");
		clearSprites.put("ghost", pathLoad.eval("clearGhost"));

		// Load GameObjects 
		GameObject.sprites.put(GameObject.Types.fruit, pathLoad.eval("fruit"));

		files = new String[]{
			"horizontal","vertical","upLeft",
			"upRight","downLeft","downRight"
		};
		Map<String, PImage> wallSprites = new HashMap<>();
		for (int i=1; i <= 6; i++) {
			wallSprites.put("" + i, pathLoad.eval(files[i-1]));
		}

		// Build map
		String[][] stringMap = Maps.parseFile(mapFile);
		this.map = new PImage[36][28];
		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (stringMap[j][i] != null) {
					if (Pattern.matches("[1-6]", stringMap[j][i])) {
						map[j][i] = wallSprites.get(stringMap[j][i]);
					}
				}
			}
		}
	}

	public void settings()
	{
		size(WIDTH, HEIGHT);
	}

	private void drawMap()
	{
		background(0, 0, 0);

		drawFruit();

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (map[j][i] != null) {
					image(map[j][i], 16 * i, 16 * j);
				}
			}
		}

		// draw scoreboard
		for (int i=0; i < lives; i++) {
			image(player.sprites.get(Direction.right), 16 + 32 * i, 543);
		}
	}

	public boolean drawFruit()
	{
		// Draw GameObjects
		for (int i=0; i < gameObjects.size(); i++) {
			GameObject obj = gameObjects.get(i);
			if (!obj.tic(player)) {
				player.points++;
				gameObjects.remove(i);
			} else {
				image(obj.getSprite(), obj.getX(), obj.getY());
			}
		}
		
		return gameObjects.size() > 0;
	}

	public void draw()
	{
		refreshMovementCache();
		drawMap();
		drawFruit();

		if (player.getDirection() != null) {
			double[] coords = player.nextCoords(player.getDirection(), 8);
			image(player.getSprite(), (float)coords[0]-5, (float)coords[1]-5);
		}
		player.tic(this, counter);

		for (int i=0; i < ghosts.size(); i++) {
			Ghost ghost = ghosts.get(i);
			if (!ghost.tic(this, player, counter)) {
				lives--;
				//counter = -1;
			}
		}
		
		counter++;
	}

	public boolean refreshMovementCache()
	{
		Direction direct = player.getDirection();
		Integer currentDirection = (direct == null) ? null : direct.KEY_CODE;
		if (keyCode == 32) {
			debugMode = (debugMode) ? false : true;
		} else if ((currentDirection == null || keyCode != currentDirection)
				&& (keyCode <= 40 && keyCode >= 37)) {
			switch (keyCode) {
				case 37:
					player.setDirection(Direction.left);
					break;
				case 38:
					player.setDirection(Direction.up);
					break;
				case 39:
					player.setDirection(Direction.right);
					break;
				case 40:
					player.setDirection(Direction.down);
					break;
			}

			return true;
		}
		return false;
	}

	public static void main(String[] args)
	{
		PApplet.main("ghost.App");
	}
}
