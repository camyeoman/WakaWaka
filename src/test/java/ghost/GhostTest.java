package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.stream.*;

public class GhostTest extends TestTools {

	@Test
	public void constructor()
	{
		Ghost ghost;

		Sprite[] ghosts = new Sprite[]{
			Sprite.ghostAmbusher,
			Sprite.ghostIgnorant,
			Sprite.ghostChaser,
			Sprite.ghostWhim
		};

		for (int j=-10; j < 10; j++) {
			for (int i=-10; i < 10; i++) {
				for (Sprite c : ghosts) {
					Ghost g = new Ghost(i, j, c);
					assertNotNull(g);
					assertTrue(g.getX() == i && g.getY() == j);
				}
			}
		}

		assertTrue((new Ghost(1,1,ghosts[0])).type == Ghost.Type.ambusher);
		assertTrue((new Ghost(1,1,ghosts[1])).type == Ghost.Type.ignorant);
		assertTrue((new Ghost(1,1,ghosts[2])).type == Ghost.Type.chaser);
		assertTrue((new Ghost(1,1,ghosts[3])).type == Ghost.Type.whim);

		for (int i=0; i < 4; i++) {
			ghost = new Ghost(1,1,Sprite.values()[i]);
			assertNotNull(ghost);
			assertNull(ghost.type);
		}

	}

	@Test
	public void setup()
	{
		// TODO sanitise maps before passing them !!!
		Configuration config = new Configuration();
		config.spriteMap = testMap;
		Ghost.SETUP(config);

		assertEquals(Ghost.SPRITE_MAP, testMap);
	}

	@Test
	public void validDirections()
	{
		Sprite[] ghosts = new Sprite[]{
			Sprite.ghostAmbusher,
			Sprite.ghostIgnorant,
			Sprite.ghostChaser,
			Sprite.ghostWhim
		};

		for (Sprite character : ghosts) {

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
	public void ambusher()
	{
		Configuration config = new Configuration();
		config.spriteMap = bigTestMap;
		config.speed = 1;
		Agent.SETUP(config);

		Point target;

		Player player = new Player(96, 96);
		Point current = new Point(80, 80);

		// scatter behaviour

		Ghost.setMode(Ghost.Mode.SCATTER);

		Point corner = Agent.TOP_RIGHT;
		player.direction = Direction.right;
		target = Ghost.ambusher(player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour
		
		Ghost.setMode(Ghost.Mode.CHASE);

		player.direction = Direction.right;
		target = Ghost.ambusher(player);
		assertTrue(target.x == player.x + 16 * 4 && target.y == player.y);

		player.direction = Direction.left;
		target = Ghost.ambusher(player);
		assertTrue(target.x == player.x - 16 * 4 && target.y == player.y);

		player.direction = Direction.down;
		target = Ghost.ambusher(player);
		assertTrue(target.x == player.x && target.y == player.y + 16 * 4);

		player.direction = Direction.up;
		target = Ghost.ambusher(player);
		assertTrue(target.x == player.x && target.y == player.y - 16 * 4);
	}

	@Test
	public void ignorant()
	{
		Configuration config = new Configuration();
		config.spriteMap = bigTestMap;
		config.speed = 1;
		Agent.SETUP(config);

		Point target;

		Player player = new Player(96, 96);
		Point current = new Point(32, 32);

		// scatter behaviour

		Ghost.setMode(Ghost.Mode.SCATTER);

		Point corner = Agent.BOT_LEFT;
		player.direction = Direction.right;

		target = Ghost.ignorant(current, player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour (in range)

		Ghost.setMode(Ghost.Mode.CHASE);

		target = Ghost.ignorant(current, player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour (out of range)

		current = new Point(200, 200);

		target = Ghost.ignorant(current, player);
		assertTrue(target.x == player.x && target.y == player.y);
	}

	@Test
	public void chaser()
	{
		Configuration config = new Configuration();
		config.spriteMap = bigTestMap;
		config.speed = 1;
		Agent.SETUP(config);

		Point target;

		Player player = new Player(32, 32);
		Point current = new Point(80, 80);

		// scatter behaviour

		Ghost.setMode(Ghost.Mode.SCATTER);

		Point corner = Agent.TOP_LEFT;
		player.direction = Direction.right;
		target = Ghost.chaser(player);
		System.out.println(target);
		assertEquals(target, corner);

		// Test chasing behaviour

		Ghost.setMode(Ghost.Mode.CHASE);

		player.direction = Direction.right;
		target = Ghost.chaser(player);
		assertTrue(target.x == player.x && target.y == player.y);
	}

	@Test
	public void whim()
	{
		Configuration config = new Configuration();
		config.spriteMap = bigTestMap;
		config.speed = 1;
		Agent.SETUP(config);

		Point target;

		Player player = new Player(32, 32);
		Point current = new Point(190, 190);

		// scatter behaviour

		Ghost.setMode(Ghost.Mode.SCATTER);

		Point corner = Agent.BOT_RIGHT;
		player.direction = Direction.right;
		target = Ghost.whim(player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// test chasing phase (no chaser)

		Ghost.setMode(Ghost.Mode.CHASE);

		player.direction = Direction.right;
		target = Ghost.whim(player);
		assertTrue(target.x == player.x && target.y == player.y);

		// TODO test other behaviour
	}

}
