package ghost;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.lang.Thread;
import java.util.Queue;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;

public class Game {
	// Configuration settings
	final int initialLives;
	final App app;

	final ModeController modeControl;
	final Sprite[][] SPRITE_MAP;

	Map<Sprite, PImage> allSprites;
	List<GameObject> GAME_OBJECTS;
	List<Ghost> GHOSTS;
	Player PLAYER;

	// Dynamic Attributes
	boolean debugMode;
	int frames;
	int points;
	int lives;
	int speed;

	public Game(App app, String configFile)
	{
		this.app = app;

		GAME_OBJECTS = new ArrayList<>();
		GHOSTS = new ArrayList<>();

		Configuration config = new Configuration(configFile);

		this.modeControl = new ModeController(config);
		this.SPRITE_MAP = config.spriteMap;

		this.initialLives = config.lives;
		this.lives = config.lives;
		this.points = 0;
		this.frames = 0;

		// Create Game entities by looping map

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (SPRITE_MAP[j][i] != null) {

					if (SPRITE_MAP[j][i] == Sprite.playerRight) {
						// create player instance
						PLAYER = new Player(16 * i, 16 * j);
					} else if (SPRITE_MAP[j][i].isGhost()) {
						// create ghost instances
						GHOSTS.add(new Ghost(16 * i, 16 * j, SPRITE_MAP[j][i]));
					} else if (SPRITE_MAP[j][i].isGameObject()) {
						// create game object instances
						GAME_OBJECTS.add(new GameObject(16 * i, 16 * j, SPRITE_MAP[j][i]));
					}

				}
			}
		}

		// setup classes
		Agent.SETUP(config);
	}

	public void loadAssets()
	{
		// load sprites
		allSprites = new HashMap<>();
		
		for (Sprite sprite : Sprite.values()) {
			allSprites.put(sprite, app.loadImage(sprite.filePath));
		}

		// load font
		char[] letters = new char[]{
			'A','E','G','I','M','N','O','R','U','V','W','Y',' '
		};

		String filePath = "src/main/resources/PressStart2P-Regular.ttf";
		app.textFont(app.createFont(filePath, 20, false, letters));
	}

	// Draw methods
	
	public void draw(Sprite sprite, int x, int y, Point target)
	{
		app.image(allSprites.get(sprite), x, y);

		if (debugMode && target != null) {
			app.beginShape();
			app.stroke(256,256,256);
			app.line(x + 12, y + 12, target.x, target.y);
			app.endShape();
		}
	}

	public void endScreen(boolean won)
	{
		app.background(0,0,0);

		if (won) {
			app.text("You won", 154, 288);
		} else {
			app.text("Game Over", 134, 288);
		}
	}

	// Tic methods

	public void softReset()
	{
		GHOSTS.stream().forEach(Agent::softReset);
		PLAYER.softReset();
		lives--;
	}

	public void hardReset()
	{

	}

	public void run()
	{
		// Draw map
		app.background(0,0,0);

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (SPRITE_MAP[j][i] != null && SPRITE_MAP[j][i].isWall()) {
					app.image(allSprites.get(SPRITE_MAP[j][i]), 16 * i, 16 * j);
				}
			}
		}

		// draw scoreboard
		for (int i=0; i < lives; i++) {
			app.image(allSprites.get(Sprite.playerRight), 8 + 27 * i, 543);
		}

		// Draw game entities

		if (PLAYER != null) {
			PLAYER.draw(this);
		}

		GAME_OBJECTS.stream()
			.filter(obj -> !obj.isEaten())
			.forEach(obj -> obj.draw(this));

		GHOSTS.stream()
			.filter(g -> g.isAlive())
			.forEach(g -> g.draw(this));

		// Update state of game

		if (app.keyCode == 32) {
			debugMode = (debugMode) ? false : true;
		}

		PLAYER.TIC(this);
		GameObject.TIC(this);
		Ghost.TIC(this);

		frames++;
	}
}
