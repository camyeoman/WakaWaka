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

	Game game;

	public PImage[][] imageMap;
	boolean debugMode = false;
	private int counter = 0;

	public App() {
	}

	public void setup() {
		game = new Game(this);
		frameRate(60);
	}

	public void settings() {
		size(WIDTH, HEIGHT);
	}

	public void draw() {
		game.drawMap(this);

		/*
		player.tic(this, counter);

		for (int i=0; i < ghosts.size(); i++) {
			Ghost ghost = ghosts.get(i);
			ghost.draw(this, counter);

			if (ghost.tic(player)) {
				game.lives--;
			}
		}
		*/

		counter++;
	}

	public static void main(String[] args)
	{
		PApplet.main("ghost.App");
	}
}
