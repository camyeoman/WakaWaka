package ghost;

import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.List;
import java.util.Map;
import java.lang.Thread;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Game {
	List<Ghost> GHOSTS;
	Player PLAYER;

	// Configuration settings
	int initialLives, lives, speed;
	boolean debugMode;
	App app;

	// Objects
	ModeController modeControl;
	List<GameObject> GAME_OBJECTS;

	// Map
	private Sprite[][] spriteMap;

	// Sprites
	Map<Sprite, PImage> allSprites;

	// Counters
	int frames = 0;
	int points = 0;

	public Game(App app)
	{
		this.app = app;

		GAME_OBJECTS = new ArrayList<>();
		GHOSTS = new ArrayList<>();

		Configuration config = new Configuration("config.json");

		this.modeControl = new ModeController(config);
		this.spriteMap = config.spriteMap;
		this.initialLives = config.lives;
		this.lives = config.lives;

		createObjects();

		// setup classes
		Agent.SETUP(config);
		Ghost.SETUP(config);
	}

	private PImage pathLoad(String str)
	{
		return app.loadImage("src/main/resources/" + str + ".png");
	}

	public void loadAssets()
	{
		// load sprites
		allSprites = new HashMap<>();
		
		// Walls
		allSprites.put(Sprite.horizontal, pathLoad("horizontal"));
		allSprites.put(Sprite.vertical, pathLoad("vertical"));
		allSprites.put(Sprite.upLeft, pathLoad("upLeft"));
		allSprites.put(Sprite.upRight, pathLoad("upRight"));
		allSprites.put(Sprite.downLeft, pathLoad("downLeft"));
		allSprites.put(Sprite.downRight, pathLoad("downRight"));

		// Player
		allSprites.put(Sprite.playerRight, pathLoad("playerRight"));
		allSprites.put(Sprite.playerLeft, pathLoad("playerLeft"));
		allSprites.put(Sprite.playerDown, pathLoad("playerDown"));
		allSprites.put(Sprite.playerUp, pathLoad("playerUp"));
		allSprites.put(Sprite.playerClosed, pathLoad("playerClosed"));

		// Ghosts
		allSprites.put(Sprite.ghostFrightened, pathLoad("frightened"));
		allSprites.put(Sprite.ghostIgnorant, pathLoad("ignorant"));
		allSprites.put(Sprite.ghostAmbusher, pathLoad("ambusher"));
		allSprites.put(Sprite.ghostChaser, pathLoad("chaser"));
		allSprites.put(Sprite.ghostWhim, pathLoad("whim"));

		// Game objects
		allSprites.put(Sprite.fruit, pathLoad("fruit"));
		allSprites.put(Sprite.superFruit, pathLoad("superFruit"));
		allSprites.put(Sprite.soda, pathLoad("soda"));

		// load font
		char[] letters = new char[]{
			'A','E','G','I','M','N','O','R','U','V','W','Y',' '
		};

		app.textFont(
			app.createFont(
				"src/main/resources/PressStart2P-Regular.ttf", 20, false, letters
			)
		);
	}

	// Active methods

	public void createObjects()
	{
		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (spriteMap[j][i] != null) {

					if (spriteMap[j][i] == Sprite.playerRight) {
						// create player instance
						PLAYER = new Player(16 * i, 16 * j);
					} else if (spriteMap[j][i].isGhost()) {
						// create ghost instances
						GHOSTS.add(new Ghost(16 * i, 16 * j, spriteMap[j][i]));
					} else if (spriteMap[j][i].isGameObject()) {
						// create game object instances
						GAME_OBJECTS.add(new GameObject(16 * i, 16 * j, spriteMap[j][i]));
					}

				}
			}
		}
	}

	// Draw methods

	public void drawMap(App app)
	{
		app.background(0,0,0);

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (spriteMap[j][i] != null && spriteMap[j][i].isWall()) {
					app.image(allSprites.get(spriteMap[j][i]), 16 * i, 16 * j);
				}
			}
		}

		// draw scoreboard
		for (int i=0; i < lives; i++) {
			app.image(allSprites.get(Sprite.playerRight), 16 + 32 * i, 543);
		}
	}

	public void drawGameObjects(App app)
	{
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

	// Tic methods

	public void softReset()
	{
		GHOSTS.stream().forEach(Agent::softReset);
		PLAYER.softReset();
		lives--;
	}

	public void drawGame(App app)
	{
		drawMap(app);
		PLAYER.draw(this, frames);

		GAME_OBJECTS.stream()
			.forEach(g -> g.draw(this));

		GHOSTS.stream()
			.filter(g -> g.alive)
			.forEach(g -> g.draw(this, frames));
	}
	
	public void tic(App app)
	{
		if (lives > 0) {
			// Draw
			drawGame(app);

			// Update
			refreshMovementCache(app.keyCode);
			app.keyCode = 0;
			Ghost.MODE = modeControl.update();

			// Player
			PLAYER.tic();

			// Game objects
			GAME_OBJECTS.stream()
				.forEach(g -> g.tic(this));

			// Ghosts
			GHOSTS.stream()
				.filter(g -> g.alive)
				.forEach(g -> g.tic(this));

			// Remove eaten game objects
			GAME_OBJECTS = GAME_OBJECTS.stream()
				.filter(obj -> !obj.isEaten())
				.collect(Collectors.toList());

			frames++;
		} else {
			endScreen(app, false);
		}
	}

	public boolean refreshMovementCache(int keyCode)
	{
		Direction direct = PLAYER.direction();
		Integer currentDirection = (direct == null) ? null : direct.KEY_CODE;

		if (keyCode == 32) {
			debugMode = (debugMode) ? false : true;
		} else if ((currentDirection == null || keyCode != currentDirection)
				&& (keyCode <= 40 && keyCode >= 37)) {
			switch (keyCode) {
				case 37:
					PLAYER.setQuedDirection(Direction.left);
					break;
				case 38:
					PLAYER.setQuedDirection(Direction.up);
					break;
				case 39:
					PLAYER.setQuedDirection(Direction.right);
					break;
				case 40:
					PLAYER.setQuedDirection(Direction.down);
					break;
			}

			return true;
		}

		return false;
	}
}
