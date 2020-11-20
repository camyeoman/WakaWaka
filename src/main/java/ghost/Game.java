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
	List<Ghost> GHOSTS = new ArrayList<>();
	Player PLAYER;

	// Configuration settings
	int initialLives, lives, speed;
	boolean debugMode;
	App app;

	// Objects
	ModeController modeControl;
	List<GameObject> gameObjects;

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
		gameObjects = new ArrayList<>();

		Configuration config = new Configuration("config.json");

		modeControl = new ModeController(config.modeLengths);

		initialLives = config.lives;
		lives = config.lives;
		spriteMap = config.spriteMap;

		createObjects();

		// setup classes
		Agent.SETUP(spriteMap, config.speed);
		Ghost.SETUP(config, PLAYER);
	}

	private PImage pathLoad(String str)
	{
		return app.loadImage("src/main/resources/" + str + ".png");
	}

	public void loadAssets()
	{
		allSprites = new HashMap<>();

		// load sprites
		allSprites.put(Sprite.horizontal, pathLoad("horizontal"));
		allSprites.put(Sprite.vertical, pathLoad("vertical"));
		allSprites.put(Sprite.upLeft, pathLoad("upLeft"));
		allSprites.put(Sprite.upRight, pathLoad("upRight"));
		allSprites.put(Sprite.downLeft, pathLoad("downLeft"));
		allSprites.put(Sprite.downRight, pathLoad("downRight"));

		allSprites.put(Sprite.playerRight, pathLoad("playerRight"));
		allSprites.put(Sprite.playerLeft, pathLoad("playerLeft"));
		allSprites.put(Sprite.playerDown, pathLoad("playerDown"));
		allSprites.put(Sprite.playerUp, pathLoad("playerUp"));
		allSprites.put(Sprite.playerClosed, pathLoad("playerClosed"));

		allSprites.put(Sprite.ghostFrightened, pathLoad("frightened"));
		allSprites.put(Sprite.ghostIgnorant, pathLoad("ignorant"));
		allSprites.put(Sprite.ghostAmbusher, pathLoad("ambusher"));
		allSprites.put(Sprite.ghostChaser, pathLoad("chaser"));
		allSprites.put(Sprite.ghostWhim, pathLoad("whim"));

		allSprites.put(Sprite.fruit, pathLoad("fruit"));
		allSprites.put(Sprite.superFruit, pathLoad("superFruit"));

		// load font
		app.textFont(
			app.createFont("src/main/resources/PressStart2P-Regular.ttf",
				20, false, new char[]{ 'G','a','m','e','O','v','e','r',' ' }
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
						PLAYER = new Player(16 * i, 16 * j);
					} else if (spriteMap[j][i].isGhost()) {
						// constructor adds to internal list
						GHOSTS.add(new Ghost(16 * i, 16 * j, spriteMap[j][i]));
					} else if (spriteMap[j][i] == Sprite.fruit) {
						gameObjects.add(new GameObject(GameObject.Type.fruit, 16 * i, 16 * j));
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
			app.image(allSprites.get(PLAYER.staticSprite()), 16 + 32 * i, 543);
		}
	}

	public void drawGameObjects(App app)
	{
		for (int i=0; i < gameObjects.size(); i++) {
			GameObject obj = gameObjects.get(i);
			obj.draw(this);
			if (obj.point().distance(PLAYER.point()) < 1) {
				points++;
				gameObjects.remove(i--);
			}
		}

		if (gameObjects.size() == 0) {
			endScreen(app, true);
		}
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

	public void tic(App app)
	{
		if (lives > 0) {
			drawMap(app);
			drawGameObjects(app);

			GHOSTS.stream()
				.filter(g -> g.alive)
				.forEach(g -> g.draw(this, frames));

			// update player direction based using input
			refreshMovementCache(app.keyCode);
			app.keyCode = 0;

			// update player
			PLAYER.tic(this, frames);

			// update ghosts
			for (Ghost ghost : GHOSTS) {
				if (!ghost.tic(this)) {
					softReset();
					break;
				}
			}

			frames++;
		} else {
			endScreen(app, false);
		}
	}

	public boolean refreshMovementCache(int keyCode)
	{
		Direction direct = PLAYER.getDirection();
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
