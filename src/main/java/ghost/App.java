package ghost;

import processing.core.PApplet;
import processing.core.PImage;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import java.util.*;

public class App extends PApplet {
	public static final int HEIGHT = 576;
	public static final int WIDTH = 448;

	Game game;

	public App(String fileName) {
		game = new Game(this, fileName);
	}

	public App() {
		game = new Game(this, "config.json");
	}

	public void setup() {
		frameRate(60);
		game.loadAssets();
	}

	public void settings() {
		size(WIDTH, HEIGHT);
	}

	public void draw() {
		game.run();
	}

	public static void main(String[] args) {
		PApplet.main("ghost.App");
	}
}
