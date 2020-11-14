package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class GhostTest {
	/* Visualisation of test map
			0123456789012
		 0*************
		 1*C        B *
		 2**********  *
		 3***  *  A*  *
		 4*D**   *    *
		 5*************
	*/

	static boolean[][] testMap = new boolean[][]
	{
		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false },
		{ false,  true,   true,   true,   true,   true,   true,   true,   true,   true,   true,   true,   false },
		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  true,   true,   false },
		{ false,  false,  false,  true,   true,   false,  true,   true,   true,   false,  true,   true,   false },
		{ false,  true,   false,  false,  true,   true,   true,   false,  true,   true,   true,   true,   false },
		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false }
	};
		
	@Test
	public void constructor()
	{
		Ghost ghost;

		for (int j=-10; j < 10; j++) {
			for (int i=-10; i < 10; i++) {
				for (char c : new char[]{'a','i','c','w'}) {
					Ghost g = new Ghost(i, j, c);
					assertNotNull(g);
					assertTrue(g.getX() == i && g.getY() == j);
				}
			}
		}

		assertTrue((new Ghost(1,1,'a')).type == Ghost.Type.ambusher);
		assertTrue((new Ghost(1,1,'i')).type == Ghost.Type.ignorant);
		assertTrue((new Ghost(1,1,'c')).type == Ghost.Type.chaser);
		assertTrue((new Ghost(1,1,'w')).type == Ghost.Type.whim);

		for (char c : new char[]{'b','d','e','f','g','h'}) {
			ghost = new Ghost(1,1,c);
			assertNotNull(ghost);
			assertNull(ghost.type);
		}

	}

	private void testValidDirect(Direction direction, Boolean[] expected, Ghost ghost)
	{
		ghost.direction = direction;

		for (int i=0; i < 4; i++) {
			Direction testDirection = Direction.values()[i];
			boolean contained = ghost.validDirections().contains(testDirection);

			if (!contained == expected[i]) {
				System.out.printf("%s should be %s\n",
					Arrays.toString(expected), ghost.validDirections()
				);
			}

			assertTrue(contained == expected[i]);
		}

		ghost.direction = null;
	}

	@Test
	public void validDirections()
	{
		Agent.boolMap = testMap;


		for (char character : new char[]{ 'a', 'c', 'i', 'w' }) {

			Ghost A = new Ghost(16 * 8,  16 * 3, character);
			Ghost B = new Ghost(16 * 10, 16 * 1, character);
			Ghost C = new Ghost(16 * 1,  16 * 1, character);
			Ghost D = new Ghost(16 * 1,  16 * 4, character);

			// Ghost A
			testValidDirect(Direction.right, new Boolean[]{ false, false, false, true },  A);
			testValidDirect(Direction.left,  new Boolean[]{ false, true, false, true  },  A);
			testValidDirect(Direction.down,  new Boolean[]{ false, true, false, true  },  A);
			testValidDirect(Direction.up,    new Boolean[]{ false, true, false, false },  A);
			// null case
			testValidDirect(null, new Boolean[]{ false, true, false, true },  A);

			// Ghost B
			testValidDirect(Direction.right, new Boolean[]{ false, false, true, true },   B);
			testValidDirect(Direction.left,  new Boolean[]{ false, true, false, true },   B);
			testValidDirect(Direction.down,  new Boolean[]{ false, true, true, true  },   B);
			testValidDirect(Direction.up,    new Boolean[]{ false, true, true, false },   B);
			// null case
			testValidDirect(null, new Boolean[]{ false, true, true, true },  B);

			// Ghost C
			testValidDirect(Direction.right, new Boolean[]{ false, false, true, false },  C);
			testValidDirect(Direction.left,  new Boolean[]{ false, false, true, false },  C);
			testValidDirect(Direction.down,  new Boolean[]{ false, false, true, false },  C);
			testValidDirect(Direction.up,    new Boolean[]{ false, false, true, false },  C);
			// null case
			testValidDirect(null, new Boolean[]{ false, false, true, false },  C);

			// Ghost D
			testValidDirect(Direction.right, new Boolean[]{ false, false, false, false }, D);
			testValidDirect(Direction.left,  new Boolean[]{ false, false, false, false }, D);
			testValidDirect(Direction.down,  new Boolean[]{ false, false, false, false }, D);
			testValidDirect(Direction.up,    new Boolean[]{ false, false, false, false }, D);
			// null case
			testValidDirect(null, new Boolean[]{ false, false, false, false },  D);
		}
	}

	@Test
	public void toggleScatter()
	{
		assertFalse(Ghost.isScatter());
		Ghost.toggleScatter();
		assertTrue(Ghost.isScatter());
		Ghost.toggleScatter();
		assertFalse(Ghost.isScatter());
		Ghost.toggleScatter();
		assertTrue(Ghost.isScatter());
	}

	@Test
	public void ambusher()
	{
		Point target, current;
		Player player;

		current = new Point(80, 80);

		player = new Player(96, 96);

		// Test chasing behaviour

		player.direction = Direction.right;
		target = Ghost.ambusher(current, player);
		assertTrue(target.x == player.x + 16 * 4 && target.y == player.y);

		player.direction = Direction.left;
		target = Ghost.ambusher(current, player);
		assertTrue(target.x == player.x - 16 * 4 && target.y == player.y);

		player.direction = Direction.down; target = Ghost.ambusher(current, player);
		assertTrue(target.x == player.x && target.y == player.y + 16 * 4);

		player.direction = Direction.up;
		target = Ghost.ambusher(current, player);
		assertTrue(target.x == player.x && target.y == player.y - 16 * 4);

		// TODO scatter behaviour
	}

	@Test
	public void chaser()
	{
		Point target, current;
		Player player;

		current = new Point(80, 80);

		// Test chasing behaviour

		player = new Player(32, 128);
		for (Direction d : Direction.values()) {
			player.direction = Direction.right;
			target = Ghost.chaser(current, player);
			assertTrue(target.x == player.x && target.y == player.y);
		}

		player = new Player(16, 64);
		for (Direction d : Direction.values()) {
			player.direction = Direction.right;
			target = Ghost.chaser(current, player);
			assertTrue(target.x == player.x && target.y == player.y);
		}

		// TODO scatter behaviour
	}
}
