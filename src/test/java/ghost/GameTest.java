package ghost;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameTest extends TestTools {

	/**
	 * Test the game contructor.
	 */
	@Test
	public void constructor() {
		App app = new App("src/test/resources/testUI.json");
		Game game = new Game(app, "src/test/resources/testUI.json");
		assertNotNull(game);
	}

	/**
	 * Tests the case where the gameObjects and ghosts lists are empty.
	 */
	@Test
	public void emptyCase() {

		App app = new App("src/test/resources/testUI.json");
		Game game = app.game;

		game.gameObjects = new ArrayList<>();
		game.ghosts = new ArrayList<>();

		App.runSketch(new String[]{"null or empty"}, app);
		app.setup();
		app.dispose();

	}

	/**
	 * Tests the functionality of Player and Ghost as relates to Game.
	 */
	@Test
	public void ticGame() {

		// Test player killing ghost

		App app = new App("src/test/resources/simpleConfig.json");
		Game game = app.game;

		Ghost ghost = game.ghosts.get(0);
		for (int i=0; ghost.getX() > 31; i++) {
			assertEquals(ghost.point(), new Point(416-i, 304));
			game.ticGame();
		}

		game.modeControl.queueMode(Ghost.Mode.FRIGHTENED);

		game.ticGame();
		assertFalse(ghost.isAlive());

		// Test ghost killing player

		app = new App("src/test/resources/simpleConfig.json");
		game = app.game;

		ghost = game.ghosts.get(0);
		for (int i=0; ghost.getX() > 31; i++) {
			assertEquals(ghost.point(), new Point(416-i, 304));
			game.ticGame();
		}

		game.ticGame();
		assertTrue(ghost.isAlive());
		assertSame(game.lives, 1);

		// Test player movement

		app = new App("src/test/resources/simpleConfig.json");
		game = app.game;

		Player player = game.player;

		app.keyCode = Direction.right.KEY_CODE;
		for (int i=0; player.getX() < 64; i++) {
			assertEquals(player.point(), new Point(16 + i, 19 * 16));
			game.ticGame();
		}

		app.keyCode = Direction.left.KEY_CODE;
		for (int i=0; player.getX() > 16; i++) {
			assertEquals(player.point(), new Point(64 - i, 19 * 16));
			game.ticGame();
		}

		// test debugMode toggle

		// toggle on
		app.keyCode = 32;
		game.ticGame();
		assertTrue(game.debugMode);

		// toggle off
		app.keyCode = 32;
		game.ticGame();
		assertFalse(game.debugMode);

	}

	/**
	 * Test the 'You Win' and 'Game Over' screens.
	 */
	@Test
	public void endScreen() {

		// loss screen

		App app = new App("src/test/resources/simpleConfig.json");
		Game game = app.game;

		game.lives = 0;
		game.checkWin();

		App.runSketch(new String[]{"lose screen"}, app);
		app.setup();
		app.delay(10);
		app.dispose();

		// win screen (with a soda can in gameObjects)

		app = new App("src/test/resources/simpleConfig.json");
		game = app.game;

		game.gameObjects = new ArrayList<>();
		game.gameObjects.add(new GameObject(0, 0, Sprite.soda));
		game.checkWin();

		App.runSketch(new String[]{"win screen with soda"}, app);
		app.setup();
		app.delay(10);
		app.dispose();

		// win screen (without a soda can in gameObjects)

		app = new App("src/test/resources/simpleConfig.json");
		game = app.game;

		game.gameObjects = new ArrayList<>();
		game.checkWin();

		App.runSketch(new String[]{"win screen w/o soda"}, app);
		app.setup();
		app.delay(10);
		app.dispose();

	}

	@Test
	public void gameObjectInteractions() {

		// invisible ghosts

		App app = new App("src/test/resources/testUI.json");
		Game game = app.game;
		Point player = game.player.point();

		// place soda under player
		GameObject soda = new GameObject(player.x,player.y,Sprite.soda);
		game.gameObjects.add(soda);
		game.ticGame();

		App.runSketch(new String[]{"invisible ghosts"}, app);
		app.setup();
		app.delay(10);
		app.dispose();

		// frightened ghosts

		app = new App("src/test/resources/testUI.json");
		game = app.game;
		Ghost.setMode(Ghost.Mode.FRIGHTENED);

		// place super fruit under player
		GameObject superFruit = new GameObject(player.x,player.y,Sprite.superFruit);
		game.gameObjects.add(superFruit);
		game.ticGame();

		App.runSketch(new String[]{"frightened ghosts"}, app);
		app.setup();
		app.delay(10);
		app.dispose();
	}

	@Test
	public void drawGame() {

		App app = new App("src/test/resources/complexConfig.json");
		Game game = app.game;

		Player player = new Player(0, 0);

		// Create game objects

		GameObject superFruit = new GameObject(0, 0, Sprite.superFruit);
		GameObject fruit = new GameObject(0, 0, Sprite.fruit);
		GameObject soda = new GameObject(0, 0, Sprite.soda);

		// set game objects to eaten
		superFruit.evolve(player);
		fruit.evolve(player);
		soda.evolve(player);

		// add to game objects
		game.gameObjects.add(superFruit);
		game.gameObjects.add(fruit);
		game.gameObjects.add(soda);

		// Create ghosts

		Ghost ambusher = new Ghost(0, 0, Sprite.ambusher);
		Ghost ignorant = new Ghost(0, 0, Sprite.ignorant);
		Ghost chaser = new Ghost(0, 0, Sprite.chaser);
		Ghost whim = new Ghost(0, 0, Sprite.whim);

		// kill ghosts
		Ghost.setMode(Ghost.Mode.FRIGHTENED);
		ambusher.evolve(player);
		ignorant.evolve(player);
		chaser.evolve(player);
		whim.evolve(player);
		Ghost.setMode(Ghost.Mode.SCATTER);

		// add to game ghost list
		game.ghosts.add(ambusher);
		game.ghosts.add(ignorant);
		game.ghosts.add(chaser);
		game.ghosts.add(whim);

		// check win with eaten objects
		game.checkWin();

		App.runSketch(new String[]{"mixed lists of game entities"}, app);
		app.setup();
		app.delay(10);
		//game.drawGame();
		app.dispose();
	}

	@Test
	public void reset() {

		App app = new App("src/test/resources/complexConfig.json");
		Game game = app.game;

		game.lives = 0;
		game.checkWin();

		app = new App("src/test/resources/complexConfig.json");
		game = app.game;

		assertSame(game.lives, 5);

		game.checkWin();
		game.reset();

		game.checkWin();

	}

	@Test
	public void run() {

		App app = new App("src/test/resources/complexConfig.json");
		Game game = app.game;

		App.runSketch(new String[]{"testing run normal"}, app);
		app.setup();
		app.delay(10);
		app.dispose();

		// with non zero wait counter

		// loss
		app = new App("src/test/resources/complexConfig.json");
		game = app.game;

		game.lives = 0;
		game.checkWin();

		App.runSketch(new String[]{"testing run wait"}, app);
		app.setup();
		app.delay(10);

		// test reset
		game.waitCounter = 1;
		app.delay(30);

		app.dispose();

		// win
		app = new App("src/test/resources/complexConfig.json");
		game = app.game;

		game.gameObjects = new ArrayList<>();
		game.checkWin();

		App.runSketch(new String[]{"testing run wait"}, app);
		app.setup();
		app.delay(10);

		// test reset
		game.waitCounter = 1;
		app.delay(30);

		app.dispose();

	}

}
