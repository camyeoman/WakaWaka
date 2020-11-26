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
	private Sprite[][] SPRITE_MAP;

	// Sprites
	Map<Sprite, PImage> allSprites;

	// Attributes
	int frames = 0;
	int points = 0;

	public Game(App app)
	{
		this.app = app;

		GAME_OBJECTS = new ArrayList<>();
		GHOSTS = new ArrayList<>();

		Configuration config = new Configuration("config.json");

		this.modeControl = new ModeController(config);
		this.SPRITE_MAP = config.spriteMap;
		this.initialLives = config.lives;
		this.lives = config.lives;

		createObjects();

		// setup classes
		Agent.SETUP(config);
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
	}

	// Draw methods
	
	public void draw(Sprite sprite, int x, int y, Point target)
	{
		app.image(allSprites.get(sprite), x, y);

		if (debugMode && target != null) {
			app.beginShape();
			app.stroke(256,256,256);
			app.line(x + 16, y + 16, target.x + 5, target.y + 5);
			app.endShape();
		}
	}

	public void drawMap(App app)
	{
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
			app.image(allSprites.get(Sprite.playerRight), 16 + 32 * i, 543);
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

	public void draw(App app)
	{
		drawMap(app);
		PLAYER.draw(this);

		GAME_OBJECTS.stream()
			.forEach(g -> g.draw(this));

		GHOSTS.stream()
			.filter(g -> g.isAlive())
			.forEach(g -> g.draw(this));
	}
	
	public void tic(int keyCode)
	{
		// Read user input

		if (app.keyCode == 32) {
			debugMode = (debugMode) ? false : true;
		}

		// Tic player
		PLAYER.evolve(app.keyCode);
		app.keyCode = 0;

		// Tic Game objects

		for (GameObject obj : GAME_OBJECTS) {
			if (!obj.tic(PLAYER)) {

				switch (obj.type) {
					case superFruit:
						GameObject.superFruit(this);
						break;
					case fruit:
						GameObject.fruit(this);
						break;
					case soda:
						GameObject.superFruit(this);
						break;
				}

			}
		}

		// Tic ghosts

		Ghost.TIC(this);

		// Remove eaten game objects
		GAME_OBJECTS = GAME_OBJECTS.stream()
			.filter(obj -> !obj.isEaten())
			.collect(Collectors.toList());

		frames++;
	}

	public void run(App app)
	{
		draw(app);
		tic(app.keyCode);

		// reset keyCode
		app.keyCode = 0;
	}
}
