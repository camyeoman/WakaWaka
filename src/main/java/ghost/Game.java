package ghost;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.lang.Thread;

import java.util.regex.Pattern;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Game {
	// Configuration settings
	List<Integer> modeLengths;
	int lives, speed;
	int initLives;

	// Objects
	List<GameObject> gameObjects;
	List<Ghost> ghosts;
	Player player;

	// Map
	Sprite[][] spriteMap;

	// Sprites
	Map<Sprite, PImage> allSprites;

	// Other
	App app;
	int counter = 0;
	int points = 0;

	public Game(App app)
	{
		this.app = app;
		gameObjects = new ArrayList<>();
		ghosts = new ArrayList<>();
	}

	private PImage pathLoad(String str)
	{
		return app.loadImage("src/main/resources/" + str + ".png");
	}

	public void setup()
	{
		Configuration data = new Configuration(app);

		allSprites = new HashMap<>();

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

		// TODO load Super fruit here!!
		allSprites.put(Sprite.fruit, pathLoad("fruit"));

		initLives = data.lives;
		lives = data.lives;
		spriteMap = data.spriteMap;

		createObjects(true);

		// setup classes
		Agent.setup(spriteMap, data.speed);
		Ghost.setup(player, data.modeLengths);

		// load font
		app.textFont(
			app.createFont("src/main/resources/PressStart2P-Regular.ttf",
				20, false, new char[]{ 'G','a','m','e','O','v','e','r',' ' }
			)
		);
	}

	public void createObjects(boolean fruit)
	{
		gameObjects = (fruit) ? new ArrayList<>() : gameObjects;

		ghosts = new ArrayList<>();

		for (int j=0; j < 36; j++) {
			for (int i=0; i < 28; i++) {
				if (spriteMap[j][i] != null) {

					if (spriteMap[j][i] == Sprite.playerRight) {
						player = new Player(16 * i, 16 * j);
					} else if (spriteMap[j][i].isGhost()) {
						ghosts.add(new Ghost(16 * i, 16 * j, spriteMap[j][i]));
					} else if (spriteMap[j][i] == Sprite.fruit && fruit) {
						gameObjects.add(new GameObject(GameObject.Type.fruit, 16 * i, 16 * j));
					}

				}
			}
		}

		Ghost.Type.PLAYER = player;
	}

	// Active methods

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
			app.image(allSprites.get(player.staticSprite()), 16 + 32 * i, 543);
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

	public void drawGameObjects(App app)
	{
		for (int i=0; i < gameObjects.size(); i++) {
			GameObject obj = gameObjects.get(i);
			obj.draw(this);
			if (obj.getPoint().distance(player.getPoint()) < 1) {
				points++;
				gameObjects.remove(i--);
			}
		}

		if (gameObjects.size() == 0) {
			endScreen(app, true);
		}
	}

	public void drawGhosts(App app)
	{
		for (int i=0; i < ghosts.size(); i++) {
			Ghost ghost = ghosts.get(i);
			ghost.draw(this, counter);

			if (ghost.tic(player, counter)) {
				counter = 0;
				lives--;
				if (lives > 0) {
					createObjects(false);
				} else {
					endScreen(app, false);
					return;
				}
			}

		}
	}

	public void tic(App app)
	{
		if (lives > 0) {
			drawMap(app);
			drawGameObjects(app);

			refreshMovementCache(app);
			player.tic(this, counter);

			drawGhosts(app);

			counter++;
		}
	}

	public boolean refreshMovementCache(App app)
	{
		Direction direct = player.getDirection();
		Integer currentDirection = (direct == null) ? null : direct.KEY_CODE;

		if (app.keyCode == 32) {
			app.debugMode = (app.debugMode) ? false : true;
			Ghost.toggleScatter();
			app.keyCode = 0;
		} else if ((currentDirection == null || app.keyCode != currentDirection)
				&& (app.keyCode <= 40 && app.keyCode >= 37)) {
			switch (app.keyCode) {
				case 37:
					player.setQuedDirection(Direction.left);
					break;
				case 38:
					player.setQuedDirection(Direction.up);
					break;
				case 39:
					player.setQuedDirection(Direction.right);
					break;
				case 40:
					player.setQuedDirection(Direction.down);
					break;
			}

			app.keyCode = 0;
			return true;
		}

		return false;
	}
}
