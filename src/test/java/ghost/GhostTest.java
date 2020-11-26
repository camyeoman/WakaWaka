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

		List<Sprite> ghosts = Arrays.stream(Sprite.values())
			.filter(sprite -> sprite.isGhost())
			.collect(Collectors.toList());

		for (int j=-10; j < 10; j++) {
			for (int i=-10; i < 10; i++) {
				for (Sprite s : ghosts) {
					Ghost g = new Ghost(i, j, s);
					assertNotNull(g);
					assertEquals(g.intialPoint, g.point());
					assertTrue(g.getX() == i && g.getY() == j);
				}
			}
		}

		Ghost ambusher = new Ghost(1,1,Sprite.ambusher);
		Ghost ignorant = new Ghost(1,1,Sprite.ignorant);
		Ghost chaser = new Ghost(1,1,Sprite.chaser);
		Ghost whim = new Ghost(1,1,Sprite.whim);

		assertEquals(ambusher.type, Ghost.Type.ambusher);
		assertEquals(ignorant.type, Ghost.Type.ignorant);
		assertEquals(chaser.type, Ghost.Type.chaser);
		assertEquals(whim.type, Ghost.Type.whim);

		assertNotNull(new Ghost(1,1,null));
	}

	@Test
	public void validDirections()
	{
		Sprite[] ghosts = new Sprite[]{
			Sprite.ambusher,
			Sprite.ignorant,
			Sprite.chaser,
			Sprite.whim
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

		// scatter behaviour

		Ghost.MODE = Ghost.Mode.SCATTER;

		Point corner = Agent.TOP_RIGHT;
		player.direction = Direction.right;
		target = Ghost.ambusher(player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour
		
		Ghost.MODE = Ghost.Mode.CHASE;

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

		Ghost ghost = new Ghost(144, 144, Sprite.ignorant);
		Player player = new Player(160, 160);

		// scatter behaviour

		Ghost.MODE = Ghost.Mode.SCATTER;

		Point corner = Agent.BOT_LEFT;
		player.direction = Direction.right;

		target = Ghost.ignorant(ghost.point(), player);
		assertEquals(ghost.target(player), target);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour (in range)

		Ghost.MODE = Ghost.Mode.CHASE;

		target = Ghost.ignorant(ghost.point(), player);
		assertEquals(ghost.target(player), target);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour (out of range)

		ghost = new Ghost(400, 400, Sprite.ignorant);

		target = Ghost.ignorant(ghost.point(), player);
		assertEquals(ghost.target(player), target);
		assertTrue(target.x == player.x && target.y == player.y);
	}

	@Test
	public void chaser()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Point target;

		Ghost ghost = new Ghost(80, 80, Sprite.chaser);
		Player player = new Player(32, 32);

		// scatter behaviour

		Ghost.MODE = Ghost.Mode.SCATTER;

		Point corner = Agent.TOP_LEFT;
		player.direction = Direction.right;
		target = Ghost.chaser(player);
		assertEquals(target, ghost.target(player));
		assertEquals(target, corner);

		// Test chasing behaviour

		Ghost.MODE = Ghost.Mode.CHASE;

		player.direction = Direction.right;
		target = Ghost.chaser(player);
		assertEquals(target, ghost.target(player));
		assertTrue(target.x == player.x && target.y == player.y);
	}

	@Test
	public void whim()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Point target;

		Ghost ghost = new Ghost(80, 80, Sprite.whim);
		Player player = new Player(32, 32);

		// scatter behaviour

		Ghost.MODE = Ghost.Mode.SCATTER;

		Point corner = Agent.BOT_RIGHT;
		player.direction = Direction.right;
		target = Ghost.whim(player);
		assertEquals(ghost.target(player), target);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// test chasing phase (no chaser)

		Ghost.MODE = Ghost.Mode.CHASE;

		player.direction = Direction.right;
		target = Ghost.whim(player);
		assertEquals(ghost.target(player), target);
		assertTrue(target.x == player.x && target.y == player.y);

		// chasing with chaser present on the map

		Ghost.setChaser(new Ghost(0, 0, Sprite.chaser));
		
		player = new Player(0, 16);
		player.direction = Direction.right;
		assertEquals(ghost.target(player), Ghost.whim(player));
		assertEquals(Ghost.whim(player), new Point(64,32));

		player = new Player(16, 48);
		player.direction = Direction.down;
		assertEquals(ghost.target(player), Ghost.whim(player));
		assertEquals(Ghost.whim(player), new Point(2*16,10*16));

		player = new Player(16, 48);
		player.direction = Direction.up;
		assertEquals(ghost.target(player), Ghost.whim(player));
		assertEquals(Ghost.whim(player), new Point(2*16,2*16));

		player = new Player(0, 16);
		player.direction = Direction.right;
		assertEquals(ghost.target(player), Ghost.whim(player));
		assertEquals(Ghost.whim(player), new Point(64,32));
	}

	@Test
	public void evolve()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		// test normal moving and basic collision
		
		// x axis
		Player player = new Player(48, 16);
		Ghost ghost = new Ghost(16, 16, Sprite.ambusher);
		Ghost chaser = new Ghost(16, 16, Sprite.chaser);

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

		// yaxis
		player = new Player(11*16, 1*16);
		ghost = new Ghost(11*16, 2*16, Sprite.ambusher);

		assertTrue(ghost.evolve(player));
		assertFalse(ghost.evolve(player));

		assertTrue(chaser.evolve(player));
		assertTrue(chaser.evolve(player));

		// Frightented mode

		ghost.setMode(Ghost.Mode.CHASE);
		chaser = new Ghost(32, 16, Sprite.chaser);
		ghost = new Ghost(32, 16, Sprite.ambusher);

		ghost.setMode(Ghost.Mode.FRIGHTENED);
		player = new Player(36, 16);
		assertNull(chaser.evolve(player));
		assertTrue(ghost.evolve(player));

		ghost.setMode(Ghost.Mode.SCATTER);
		assertFalse(chaser.evolve(player));
		assertFalse(ghost.evolve(player));
	}

	@Test
	public void softReset()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));

		Ghost ghost = new Ghost(16, 16, Sprite.chaser);
		Player player = new Player(48, 16);

		for (int i=0; i < 16; i++) {
			ghost.evolve(player);
		}

		assertEquals(ghost.point(), new Point(32, 16));
		ghost.softReset();
		assertEquals(ghost.point(), new Point(16, 16));
	}

	@Test
	public void getSprite()
	{
		Agent.SETUP(new Configuration("src/test/resources/config1.json"));


		for (Sprite sprite : Sprite.values()) {

			Ghost ghost = new Ghost(16, 16, sprite);

			if (sprite.isGhost()) {
				Ghost.MODE = Ghost.Mode.SCATTER;
				assertEquals(ghost.getSprite(), sprite);

				Ghost.MODE = Ghost.Mode.CHASE;
				assertEquals(ghost.getSprite(), sprite);

				Ghost.MODE = null;
				assertEquals(ghost.getSprite(), sprite);

				Ghost.MODE = Ghost.Mode.FRIGHTENED;
				assertEquals(ghost.getSprite(), Sprite.frightened);

				Ghost.MODE = Ghost.Mode.INVISIBLE;
				assertEquals(ghost.getSprite(), Sprite.invisible);
			} else {

				for (Ghost.Mode mode : Ghost.Mode.values()) {
					Ghost.MODE = mode;
					assertNull(ghost.getSprite());
				}

			}
		}

	}

	@Test
	public void TIC() {
		App app = new App();
		Game game = new Game(app, "config.json");
	}
}
