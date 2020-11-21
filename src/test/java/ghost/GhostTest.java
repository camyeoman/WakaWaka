package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.stream.*;

public class GhostTest {
	/*
	@Test
	public void constructor()
	{
		Agent.setup(testMap, 1);

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
		Agent.setup(testMap, 1);
		// TODO sanitise maps before passing them !!!
		Ghost.setup(new Player(1, 1), new ArrayList<>(), new ArrayList<>());
	}

	@Test
	public void validDirections()
	{
		Agent.setup(testMap, 1);

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
		Agent.setup(bigTestMap, 1);
		Point target;

		Player player = new Player(96, 96);
		Point current = new Point(80, 80);
		Ghost.setup(player, new ArrayList<>(), new ArrayList<>());

		// scatter behaviour

		Point corner = Ghost.Type.TOP_RIGHT;
		player.direction = Direction.right;
		target = Ghost.ambusher(current, player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour
		
		Ghost.toggleScatter();

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
	}

	@Test
	public void ignorant()
	{
		Agent.setup(bigTestMap, 1);
		Point target;

		Player player = new Player(96, 96);
		Point current = new Point(32, 32);

		Ghost.setup(player, new ArrayList<>(), new ArrayList<>());

		// scatter behaviour

		Point corner = Ghost.Type.BOT_LEFT;
		player.direction = Direction.right;

		target = Ghost.ignorant(current, player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour (in range)

		Ghost.toggleScatter();

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
		Agent.setup(testMap, 1);
		Point target;

		Player player = new Player(32, 32);
		Point current = new Point(80, 80);
		Ghost.setup(player, new ArrayList<>(), new ArrayList<>());

		// scatter behaviour

		Point corner = Ghost.Type.TOP_LEFT;
		player.direction = Direction.right;
		target = Ghost.chaser(current, player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// Test chasing behaviour

		Ghost.toggleScatter();

		player.direction = Direction.right;
		target = Ghost.chaser(current, player);
		assertTrue(target.x == player.x && target.y == player.y);
	}

	@Test
	public void whim()
	{
		Agent.setup(bigTestMap, 1);
		Point target;

		Player player = new Player(32, 32);
		Point current = new Point(190, 190);
		Ghost.setup(player, new ArrayList<>(), new ArrayList<>());

		// scatter behaviour

		Point corner = Ghost.Type.BOT_RIGHT;
		player.direction = Direction.right;
		target = Ghost.whim(current, player);
		assertTrue(target.x == corner.x && target.y == corner.y);

		// test chasing phase (no chaser)

		Ghost.toggleScatter();

		player.direction = Direction.right;
		target = Ghost.whim(current, player);
		assertTrue(target.x == player.x && target.y == player.y);

		// TODO test other behaviour
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

	static Sprite[][] testMap;
	static // test map
	{
		String[][] stringMap = new String[][] {
			{ "3","1","1","1","1","1","1","1","1","1","1","1","4" },
			{ "2","p","a","c","i","w","7","7","7","7","7","7","2" },
			{ "2","1","1","1","1","1","1","1","1","4","7","7","2" },
			{ "2","2","2","7","7","2","7","7","7","2","7","7","2" },
			{ "2","7","2","2","7","7","7","2","7","7","7","7","2" },
			{ "5","1","1","1","1","1","1","1","1","1","1","1","6" }
		};

		testMap = new Sprite[6][13];

		for (int j=0; j < 6; j++) {
			for (int i=0; i < 13; i++) {
				testMap[j][i] = Sprite.getSprite(stringMap[j][i]);
			}
		}
	}

	static Sprite[][] bigTestMap;
	static // test map
	{
		String[][] stringMap = new String[][] {
			{ "3","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","4" },
			{ "2","p","a","c","i","w","7","7","7","7","7","7","7","7","7","7","2" },
			{ "2","1","1","1","1","1","1","1","1","1","1","1","1","4","7","7","2" },
			{ "2","2","2","7","7","2","7","7","7","7","7","7","7","2","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "2","7","2","2","7","7","7","2","2","2","2","2","7","7","7","7","2" },
			{ "5","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","6" }
		};


		bigTestMap = new Sprite[stringMap.length][stringMap[0].length];

		for (int j=0; j < bigTestMap.length; j++) {
			for (int i=0; i < bigTestMap[0].length; i++) {
				bigTestMap[j][i] = Sprite.getSprite(stringMap[j][i]);
			}
		}
	}
	*/
}
