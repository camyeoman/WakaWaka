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

import java.util.ArrayList;
import java.util.List;

public class GameObjectTest {

	/**
	 * Test the constructor for different types of gameObjects.
	 */
	@Test
	public void constructor() {

		GameObject obj;

		for (GameObject.Type type : GameObject.Type.values()) {
			obj = new GameObject(0, 0, type.sprite);
			assertNotNull(obj);
			assertNotNull(obj.getSprite());
			assertEquals(obj.type, type);
		}

		obj = new GameObject(0, 0, null);
		assertNull(obj.getSprite());

	}

	/**
	 * Test a player object eating a gameObject.
	 */
	@Test
	public void eatingObject() {

		for (GameObject.Type type : GameObject.Type.values()) {
			Player closePlayer = new Player(0, 0);
			GameObject obj = new GameObject(0, 0, type.sprite);
			assertFalse(obj.evolve(closePlayer));
			assertTrue(obj.isEaten());

			// reset object
			obj.reset();
			assertFalse(obj.isEaten());


			Player farPlayer = new Player(100, 50);
			obj = new GameObject(0, 0, type.sprite);
			assertTrue(obj.evolve(farPlayer));
			assertFalse(obj.isEaten());
			obj.reset();
			assertFalse(obj.isEaten());
		}

	}

	/**
	 * Test game interaction with eating gameObjects.
	 */
	@Test
	public void interactions() {

		App app = new App("src/test/resources/simpleConfig.json");
		Game game = app.game;

		int x = game.player.getX();
		int y = game.player.getY();

		Ghost ghost = game.ghosts.get(0);

		GameObject superFruit = new GameObject(x, y, Sprite.superFruit);
		GameObject fruit = new GameObject(x, y, Sprite.fruit);
		GameObject soda = new GameObject(x, y, Sprite.soda);

		// test fruit interaction
		game.gameObjects = new ArrayList<>();
		game.gameObjects.add(fruit);

		GameObject.TIC(game);
		assertSame(game.points, 1);

		// test super fruit interaction
		game.gameObjects = new ArrayList<>();
		game.gameObjects.add(superFruit);

		GameObject.TIC(game);
		game.ticGame();
		// assert is frightened sprite, to verify the mode
		assertEquals(ghost.getSprite(), Sprite.frightened);

		// test soda interaction
		game.gameObjects = new ArrayList<>();
		game.gameObjects.add(soda);

		GameObject.TIC(game);
		game.ticGame();
		// assert is invisible sprite, to verify the mode
		assertEquals(ghost.getSprite(), Sprite.invisible);

	}

	/**
	 * Test the draw method for each type of game object.
	 */
	@Test
	public void draw() {

		App app = new App("src/test/resources/simpleConfig.json");
		Game game = app.game;

		int x = game.player.getX();
		int y = game.player.getY();

		GameObject superFruit = new GameObject(x+32, y, Sprite.superFruit);
		GameObject fruit = new GameObject(x+48, y, Sprite.fruit);
		GameObject soda = new GameObject(x+64, y, Sprite.soda);

		game.gameObjects.add(fruit);
		game.gameObjects.add(superFruit);
		game.gameObjects.add(soda);

		App.runSketch(new String[]{"App"}, app);
		app.setup();

		for (GameObject obj : game.gameObjects) {
			obj.draw(game);
		}

		app.dispose();

	}

}
