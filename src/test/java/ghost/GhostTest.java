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
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Point target;

		Player player = new Player(160, 160);
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
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Point target;

		Player player = new Player(160, 160);
		Point current = new Point(144, 144);

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

		current = new Point(400, 400);

		target = Ghost.ignorant(current, player);
		assertTrue(target.x == player.x && target.y == player.y);
	}

	@Test
	public void chaser()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Point target;

		Player player = new Player(32, 32);
		Point current = new Point(80, 80);

		// scatter behaviour

		Ghost.setMode(Ghost.Mode.SCATTER);

		Point corner = Agent.TOP_LEFT;
		player.direction = Direction.right;
		target = Ghost.chaser(player);
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
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

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

		// chasing with chaser present on the map

		Ghost.setChaser(new Ghost(0, 0, Sprite.ghostChaser));
		
		player = new Player(0, 16);
		player.direction = Direction.right;
		assertEquals(Ghost.whim(player), new Point(64,32));

		player = new Player(16, 48);
		player.direction = Direction.down;
		assertEquals(Ghost.whim(player), new Point(2*16,10*16));

		player = new Player(16, 48);
		player.direction = Direction.up;
		assertEquals(Ghost.whim(player), new Point(2*16,2*16));

		player = new Player(0, 16);
		player.direction = Direction.right;
		assertEquals(Ghost.whim(player), new Point(64,32));
	}

	@Test
	public void tic()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Player player = new Player(48, 16);
		Ghost ghost = new Ghost(16, 16, Sprite.ghostAmbusher);
		Ghost chaser = new Ghost(16, 16, Sprite.ghostChaser);

		// test normal moving and basic collision

		for (int i=0; true; i++) {
			Direction direction = (i==0) ? null : Direction.right;
			assertEquals(ghost.toString(), getString(16+i, 16, direction));
			assertEquals(chaser.toString(), getString(16+i, 16, direction));
			if (48 - (16 + i) < 16) {
				assertFalse(ghost.evolve(player));
				assertFalse(chaser.evolve(player));
				break;
			} else {
				assertTrue(ghost.evolve(player));
				assertTrue(chaser.evolve(player));
			}
		}

		Ghost.setMode(Ghost.Mode.CHASE);
		chaser = new Ghost(32, 16, Sprite.ghostChaser);
		ghost = new Ghost(32, 16, Sprite.ghostChaser);

		Ghost.setMode(Ghost.Mode.CHASE);
		assertTrue(chaser.evolve(player));
		assertTrue(ghost.evolve(player));
		Ghost.setMode(Ghost.Mode.FRIGHTENED);
		assertNull(chaser.evolve(player));
		assertTrue(ghost.evolve(player));
	}
}
