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

	public App() {
		game = new Game(this);
	}

	public void setup() {
		frameRate(60);
		game.loadAssets();
	}

	public void settings() {
		size(WIDTH, HEIGHT);
	}

	public void draw() {
		if (game.lives < 1) {
			try {
				Thread.sleep(10 * 1000);
			} catch (Exception e) {}

			//game = new Game(this);
		}

		game.run(this);
	}

	public static void main(String[] args) {
		PApplet.main("ghost.App");
	}
}
