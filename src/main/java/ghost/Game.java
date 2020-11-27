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
	final ModeController modeControl;
	final Sprite[][] SPRITE_MAP;
	final int initialLives;
	final App app;

	Map<Sprite, PImage> allSprites;
	List<GameObject> GAME_OBJECTS;
	List<Point> corners;
	List<Ghost> GHOSTS;
	Player PLAYER;

	// Dynamic Attributes

	boolean debugMode;
	Boolean won;

	int waitCounter;
	int frames;
	int points;
	int lives;

	public Game(App app, String configFile)
	{
		this.app = app;

		// null means in progress, i.e not won or lost
		this.won = null;

		GAME_OBJECTS = new ArrayList<>();
		GHOSTS = new ArrayList<>();
		corners = new ArrayList<>();

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

		List<Ghost> chasers = GHOSTS.stream()
			.filter(g -> g.isAlive() && g.type == Ghost.Type.chaser)
			.collect(Collectors.toList());

		if (chasers.size() > 0) {
			Ghost.setChaser(chasers.get(0));
		}
	}


	public void loadAssets()
	{
		// load sprites
		allSprites = new HashMap<>();
		
		for (Sprite sprite : Sprite.values()) {
			if (sprite != null) {
				allSprites.put(sprite, app.loadImage(sprite.filePath));
			}
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
		if (sprite == null) {
			return;
		}

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

	public void reset()
	{
		// Reset positions and attributes
		GHOSTS.stream().forEach(Agent::reset);
		PLAYER.reset();

		if (lives > 0) {
			lives--;
		}

		if (won != null) {

		}
	}

	public void checkWin()
	{
		int objectsLeft = (int) GAME_OBJECTS.stream()
			.filter(obj -> !obj.isEaten())
			.count();

		if (objectsLeft == 0 || lives == 0) {

			// store result of game
			won = (lives == 0) ? false : true;

			// reset all game entities
			GHOSTS.stream().forEach(Agent::reset);
			PLAYER.reset();
			GAME_OBJECTS.stream().forEach(GameObject::reset);

			// reset game attributes
			frames = 0;
			points = 0;
			lives = initialLives;

			// Que 10 seconds of waiting with end screen
			waitCounter = 60 * 10;
		}
	}

	public void drawGame()
	{
		// Draw map
		app.background(0,0,0);

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (SPRITE_MAP[j][i] != null && SPRITE_MAP[j][i].isWall()) {
					if (allSprites.get(SPRITE_MAP[j][i]) != null) {
						app.image(allSprites.get(SPRITE_MAP[j][i]), 16 * i, 16 * j);
					}
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

		for (GameObject obj : GAME_OBJECTS) {
			if (!obj.isEaten()) {
				obj.draw(this);
			}
		}

		for (Ghost ghost : GHOSTS) {
			if (ghost.isAlive()) {
				ghost.draw(this);
			}
		}
	}

	public void ticGame()
	{
		Ghost.MODE = modeControl.update();

		if (app.keyCode == 32) {
			debugMode = (debugMode) ? false : true;
		}

		PLAYER.TIC(this);
		GameObject.TIC(this);
		Ghost.TIC(this);

		frames++;
	}

	public void run()
	{
		if (waitCounter < 1) {
			if (won != null) {
				won = null;
			}

			drawGame();
			ticGame();
			checkWin();
		} else {
			// only need to draw once
			if (waitCounter == 60 * 10) {
				endScreen(won);
			}

			waitCounter--;
		}
	}
}
