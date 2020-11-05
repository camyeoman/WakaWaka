package ghost;
import processing.core.PApplet;
import processing.core.PImage;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import java.util.*;
import java.util.regex.*;

public class App extends PApplet {
	public static final int HEIGHT = 576;
	public static final int WIDTH = 448;

	// move these
	public int[] modeLengths;

	public String fileName;
	public int lives;
	public List<GameObject> gameObjects = new ArrayList<>();
	public List<Ghost> ghosts = new ArrayList<>();
	public Player player = null;
	public PImage[][] map;

	private boolean debugMode = false;
	private int counter = -1;

	public App() {
		fileName = Utilities.parseConfig(this);
	}

	public void setup() {
		frameRate(60);
		Utilities.populateClasses(this, fileName);
		Utilities.loadSprites(this);
	}

	public void settings() {
		size(WIDTH, HEIGHT);
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

	private void drawMap() {
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
			image(player.scoreboardSprite(), 16 + 32 * i, 543);
		}
	}

	public void draw()
	{
		refreshMovementCache();
		drawMap();
		drawFruit();

		/*
		if (player.getDirection() != null) {
			int[] coords = player.nextCoords(player.getDirection(), 8);
			image(player.getSprite(counter), (float)coords[0]-5, (float)coords[1]-5);
		}
		*/

		player.tic(this, counter);

		for (int i=0; i < ghosts.size(); i++) {
			Ghost ghost = ghosts.get(i);
			if (!ghost.tic(this, player, counter)) {
				lives--;
			}
		}
	}

	public boolean refreshMovementCache()
	{
		counter++;

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
