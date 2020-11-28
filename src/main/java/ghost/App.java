package ghost;

import processing.core.PApplet;
import processing.core.PImage;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 

/**
 * Rendering class.
 */
public class App extends PApplet {
	static final int HEIGHT = 576;
	static final int WIDTH = 448;

	/**
	 * Instance of game, manages all logic.
	 */
	final Game game;

	/**
	 * Initialise a App instance from a specific config file.
	 * @param fileName, the file path to a json config file
	 */
	public App(String fileName) {
		game = new Game(this, fileName);
	}

	/**
	 * Default constructor using 'config.json'.
	 */
	public App() {
		game = new Game(this, "config.json");
	}

	/**
	 * Load PImages and PFont.
	 */
	public void setup() {
		frameRate(60);
		game.loadAssets();
	}

	/**
	 * Initialise size of game window
	 */
	public void settings() {
		size(WIDTH, HEIGHT);
	}

	/**
	 * Main loop of program called 60 times a second.
	 */
	public void draw() {
		game.run();
	}

	public static void main(String[] args) {
		PApplet.main("ghost.App");
	}
}
