package ghost;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.lang.Thread;
import java.util.Queue;
import java.util.List;
import java.util.Map;

import processing.core.PImage;
import processing.core.PFont;

/**
 * The game instance controlling all logic.
 */
public class Game {

	// Configuration settings

	/**
	 * ModeController object to control mode.
	 */
	final ModeController modeControl;

	/**
	 * The map as represented by an array of Sprites.\
	 */
	final Sprite[][] SPRITE_MAP;

	/**
	 * The initial lives from a config file.
	 */
	final int initialLives;

	/**
	 * The associated App for rendering.
	 */
	final App app;

	/**
	 * Link sprite objects to PImages for rendering.
	 */
	Map<Sprite, PImage> allSprites;

	/**
	 * List of all game objects in game instance.
	 */
	List<GameObject> gameObjects;

	/**
	 * List of all ghosts in game instance.
	 */
	List<Ghost> ghosts;

	/**
	 * Current player of game instance.
	 */
	Player player;

	// Dynamic Attributes

	/**
	 * If debug mode is active.
	 */
	boolean debugMode;

	/**
	 * If the game has been won.
	 */
	boolean won;

	/**
	 * Counter for end screen, representing how long to wait.
	 */
	int waitCounter;

	/**
	 * Number of frames elapsed in game instance.
	 */
	int frames;

	/**
	 * Number of points in game instance.
	 */
	int points;

	/**
	 * Number of lives in game instance.
	 */
	int lives;

	/**
	 * Initialise game from config file.
	 * @param app, rendering app object
	 * @param configFile, config file to parse
	 */
	public Game(App app, String configFile) {

		// setup game instance
		this.app = app;
		this.won = false;

		gameObjects = new ArrayList<>();
		ghosts = new ArrayList<>();

		// create config object
		Configuration config = new Configuration(configFile);

		// setup classes
		Agent.SETUP(config);

		// setup game instance
		this.modeControl = new ModeController(config);
		this.SPRITE_MAP = config.spriteMap;

		this.initialLives = config.lives;
		this.lives = config.lives;
		this.waitCounter = 0;
		this.points = 0;
		this.frames = 0;

		// Create Game entities by looping map

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (SPRITE_MAP[j][i] != null) {

					if (SPRITE_MAP[j][i] == Sprite.playerRight) {
						// create player instance
						player = new Player(16 * i, 16 * j);
					} else if (SPRITE_MAP[j][i].isGhost()) {
						// create ghost instances
						ghosts.add(new Ghost(16 * i, 16 * j, SPRITE_MAP[j][i]));
					} else if (SPRITE_MAP[j][i].isGameObject()) {
						// create game object instances
						gameObjects.add(new GameObject(16 * i, 16 * j, SPRITE_MAP[j][i]));
					}

				}
			}
		}

		// get CHASER
		for (Ghost ghost : ghosts) {
			if (ghost.type == Ghost.Type.chaser) {
				Ghost.setChaser(ghost);
				break;
			}
		}

	}

	/**
	 * Loads PImages, and links them to their Sprite equivalent, and loads the
	 * PFont for the end screen.
	 */
	public void loadAssets() {

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
	
	/**
	 * Draw a game entity, with infrastructure for debug mode with the target
	 * point.
	 * @param sprite, sprite to render
	 * @param x, the integer x coordinate
	 * @param y, the integer y coordinate
	 * @param target, the target for the debug mode line
	 */
	public void draw(Sprite sprite, int x, int y, Point target) {

		app.image(allSprites.get(sprite), x, y);

		if (debugMode && target != null) {
			app.beginShape();
			app.stroke(256,256,256);
			app.line(x + 12, y + 12, target.x, target.y);
			app.endShape();
		}

	}

	// Tic methods

	/**
	 * Reset the game entity positions and decrement the game lives, if lives
	 * greater than 0.
	 */
	public void reset() {

		// Reset positions and attributes
		ghosts.stream().forEach(Agent::reset);
		player.reset();

		// decrement game lives
		lives = (lives > 0) ? lives - 1 : lives;

	}

	/**
	 * Check if the game has been won or lost, then it is over transition to
	 * end screen for 10 seconds.
	 */
	public void checkWin() {

		int objectsLeft = (int) gameObjects.stream()
			.filter(obj -> !obj.isEaten() && obj.type != GameObject.Type.soda)
			.count();

		if (objectsLeft == 0 || lives == 0) {

			// store result of game
			won = (lives == 0) ? false : true;

			// reset all game entities
			ghosts.stream().forEach(Agent::reset);
			player.reset();
			gameObjects.stream().forEach(GameObject::reset);

			// reset game attributes
			frames = 0;
			points = 0;
			lives = initialLives;

			// Que 10 seconds of waiting with end screen
			waitCounter = 60 * 10;
		}

	}

	/**
	 * Draw the game map, background, scoreboard, gameObjects, ghosts and player.
	 */
	public void drawGame() {

		// Draw map
		app.background(0,0,0);

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (SPRITE_MAP[j][i] != null && SPRITE_MAP[j][i].isWall()) {
					draw(SPRITE_MAP[j][i], 16 * i, 16 * j, null);
				}
			}
		}

		// draw scoreboard
		for (int i=0; i < lives; i++) {
			draw(Sprite.playerRight, 8 + 27 * i, 543, null);
		}

		// Draw game entities

		player.draw(this);

		gameObjects.stream()
			.filter(obj -> !obj.isEaten())
			.forEach(obj -> obj.draw(this));

		ghosts.stream()
			.filter(ghost -> ghost.isAlive())
			.forEach(ghost -> ghost.draw(this));

	}

	/**
	 * Update game entities, debug mode and increment the number of frames by 1.
	 */
	public void ticGame() {

		Ghost.setMode(modeControl.update());

		debugMode = (app.keyCode == 32) ? !debugMode : debugMode;

		player.TIC(this);
		GameObject.TIC(this);
		Ghost.TIC(this);

		frames++;

	}

	/**
	 * Core loop to update and draw the game.
	 */
	public void run() {

		if (waitCounter == 0) {
			drawGame();
			ticGame();
			checkWin();
		} else {

			// draw win/loss screen

			app.background(0,0,0);

			if (won) {
				app.text("You won", 154, 288);
			} else {
				app.text("Game Over", 134, 288);
			}

			// increment counter and if wait is over reset game

			if (waitCounter > 1) {
				waitCounter--;
			} else {
				gameObjects.stream().forEach(GameObject::reset);
				won = false;
				reset();
			}

		}

	}

}
