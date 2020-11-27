package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class PlayerTest extends TestTools {
	/* Visualisation of test map
			0123456789012
		 0*************
		 1*           *
		 2**********  *
		 3***  *   *  *
		 4* **   *    *
		 5*************
	*/
	@Test
	public void constructor()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Player player = new Player(1, 1);
		assertNotNull(player);
	}

	@Test
	public void getSprite()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		int counter = 0;
		Player player = new Player(16, 16);
		for (Direction d : Direction.values()) {
			player.direction = d;
			counter = 8;
			//assertEquals(player.getSprite(counter++), player.sprites.get(d));
			//assertEquals(player.getSprite(counter), player.sprites.get(d));
		}

	}

	@Test
	public void reset() {
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Player player = new Player(16, 16);

		// Keycodes
		// Left - 37
		// Up - 38
		// Right - 39
		// Down - 40

		// set direction to right
		for (int i=0; i < 16; i++) {
			player.evolve(39);
			assertEquals(player.direction(), Direction.right);
		}

		assertEquals(player.point(), new Point(32, 16));
		player.reset();
		assertEquals(player.point(), new Point(16, 16));
	}

	@Test
	public void evolve() {
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Player player = new Player(16, 16);

		// direction is null case (start of game)
		player.evolve(0);
		assertEquals(player.point(), new Point(16,16));
		assertEquals(player.queuedDirection(), null);
		assertEquals(player.direction(), null);

		for (int i=0; i < 16*10; i++) {
			player.evolve(Direction.right.KEY_CODE);
			assertEquals(player.direction(), Direction.right);
			assertEquals(player.point(), new Point(16 + (i+1), 16));
		}

		// walk into wall
		for (int i=0; i < 10; i++) {
			player.evolve(Direction.right.KEY_CODE);
			assertEquals(player.direction(), Direction.right);
			assertEquals(player.point(), new Point(16*11, 16));
		}

		for (int i=0; i < 16; i++) {
			player.evolve(Direction.left.KEY_CODE);
			assertEquals(player.direction(), Direction.left);
			assertEquals(player.point(), new Point(16*11 - (i+1), 16));
		}

		for (int i=0; i < 16 * 3; i++) {
			player.evolve(Direction.down.KEY_CODE);
			assertEquals(player.direction(), Direction.down);
			assertEquals(player.point(), new Point(16*10, 16 + (i+1)));
		}

		// walk into wall
		for (int i=0; i < 10; i++) {
			player.evolve(Direction.down.KEY_CODE);
			assertEquals(player.direction(), Direction.down);
			assertEquals(player.point(), new Point(16*10, 64));
		}

		for (int i=0; i < 16 * 3; i++) {
			player.evolve(Direction.up.KEY_CODE);
			assertEquals(player.direction(), Direction.up);
			assertEquals(player.point(), new Point(16*10, 16 * 4 - (i+1)));
		}
	}

	
	@Test
	public void keyboardInput() {
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		// Keycodes
		// Left - 37
		// Up - 38
		// Right - 39
		// Down - 40

		// show that the direction keyCodes are correct
		assertSame(Direction.left.KEY_CODE, 37);
		assertSame(Direction.up.KEY_CODE, 38);
		assertSame(Direction.right.KEY_CODE, 39);
		assertSame(Direction.down.KEY_CODE, 40);

		Player player = new Player(16, 16);

		for (Direction direction : Direction.values()) {
			assertTrue(player.keyboardInput(direction.KEY_CODE));
			assertEquals(player.queuedDirection(), direction);
		}

		// test under a range of integers

		player.keyboardInput(Direction.left.KEY_CODE);
		Direction direction = Direction.left;
		for (int i=-50; i < 50; i++) {
			if (i >= 37 && i <= 40) {
				assertTrue(player.keyboardInput(i));
				direction = player.queuedDirection();
			} else {
				assertFalse(player.keyboardInput(i));
				assertEquals(player.queuedDirection(), direction);
			}
		}

	}

}
