package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

interface lambdaTest<T, U, V> {
	public void with(T a, U b, V c);
}

public class AgentTest {

	@Test
	public void constructor()
	{
		Agent agent = new Agent(1, 1);
		assertNotNull(agent);
	}

	@Test
	public void translate()
	{
		Agent.setup(testMap, 1);

		lambdaTest<Integer, Integer, Point> pointCheck
			= (a, b, point) -> assertTrue(point.x == a && point.y == b);

		Agent agent = new Agent(16, 16);

		pointCheck.with(16,16, agent.translate(null, 1));

		// test right
		for (int i=-20; i < 20; i++) {
			pointCheck.with(16+i, 16, agent.translate(Direction.right, i));
		}

		// test left
		for (int i=-20; i < 20; i++) {
			pointCheck.with(16-i, 16, agent.translate(Direction.left, i));
		}

		// test down
		for (int i=-20; i < 20; i++) {
			pointCheck.with(16, 16+i, agent.translate(Direction.down, i));
		}

		// test up
		for (int i=-20; i < 20; i++) {
			pointCheck.with(16, 16-i, agent.translate(Direction.up, i));
		}
	}

	@Test
	public void isWall()
	{
		Agent.setup(testMap, 1);

		lambdaTest<Direction, Boolean, Agent> checkWall
			= new lambdaTest<Direction, Boolean, Agent>() {
			public void with(Direction d, Boolean bool, Agent agent) {
				Point point = agent.translate(d, 1);
				point = point.gridSnap(d);
				assertTrue(agent.isWall(point) == bool);
			}
		};

		Agent agent;

		// Check out of bounds behaviour, treat cells
		// outside of grid as effectively 'walls'
		agent = new Agent(-32, 16);
		checkWall.with(Direction.left, true, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, true, agent);

		// Check behaviour in grid
		agent = new Agent(16, 16);
		checkWall.with(Direction.left, true, agent);
		checkWall.with(Direction.right, false, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, true, agent);

		agent = new Agent(16, 16 * 4);
		checkWall.with(Direction.left, true, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, true, agent);

		agent = new Agent(16 * 11, 16);
		checkWall.with(Direction.left, false, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, false, agent);

		agent = new Agent(16 * 11, 16);
		checkWall.with(Direction.left, false, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, false, agent);

		agent = new Agent(16 * 11, 16 * 4);
		checkWall.with(Direction.left, false, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, false, agent);
		checkWall.with(Direction.down, true, agent);
	}

	@Test
	public void validDirection()
	{
		Agent.setup(testMap, 1);

		Agent agent;

		// test behavuiour on grid squares, i.e % 16 == 0
		testValidDirection(new Boolean[]{false, true, false, true}, new Agent(16*8, 16*3));
		testValidDirection(new Boolean[]{false, false, false, false}, new Agent(16, 16*4));
		testValidDirection(new Boolean[]{false, true, true, true}, new Agent(16*10, 16));
		testValidDirection(new Boolean[]{false, false, true, false}, new Agent(16, 16));

		// TEST FOR X, Y % 16 != 0 (not clearly on grid squares)
		agent = new Agent(16 * 9 - 5, 16);
		agent.direction = Direction.left;
		testValidDirection(new Boolean[]{ false, true, true, false }, agent);

		agent = new Agent(16 * 8, 16 * 3 + 7);
		agent.direction = Direction.down;
		testValidDirection(new Boolean[]{ true, false, false, true }, agent);

		agent = new Agent(16 * 9 + 9, 16);
		agent.direction = Direction.right;
		testValidDirection(new Boolean[]{ false, true, true, false }, agent);

		agent = new Agent(16 * 8, 16 * 3 - 3);
		agent.direction = Direction.down;
		testValidDirection(new Boolean[]{ true, false, false, true }, agent);

		// test for direction null case at beginning of game
		agent = new Agent(16 + 3, 16);
		testValidDirection(new Boolean[]{ false, false, false, false }, agent);

		// test if direction given is null
		agent = new Agent(16 + 3, 16);
		assertFalse(agent.validDirection(null));
	}

	@Test
	public void validDirections()
	{
		Agent.setup(testMap, 1);

		Agent agent;

		testValidDirections(new Boolean[]{false, true, false, true}, new Agent(16*8, 16*3));
		testValidDirections(new Boolean[]{false, false, false, false}, new Agent(16, 16*4));
		testValidDirections(new Boolean[]{false, true, true, true}, new Agent(16*10, 16));
		testValidDirections(new Boolean[]{false, false, true, false}, new Agent(16, 16));

		// TEST FOR X, Y % 16 != 0 (not clearly on grid squares)
		agent = new Agent(16 * 9 - 5, 16);
		agent.direction = Direction.left;
		testValidDirections(new Boolean[]{ false, true, true, false }, agent);

		agent = new Agent(16 * 8, 16 * 3 + 7);
		agent.direction = Direction.down;
		testValidDirections(new Boolean[]{ true, false, false, true }, agent);
	}

	@Test
	public void getterAndSetterMethods()
	{
		Agent.setup(testMap, 1);

		int[][] tests = new int[][]{
			{16,  16}, {32,  16}, {48,  16},
			{64,  16}, {80,  16}, {96,  16}
		};

		for (int[] coord : tests) {
			int a = coord[0], b = coord[1];
			Agent agent = new Agent(a, b);
			Direction direction = agent.getDirection();

			assertEquals(agent.displayX(), a - 5); 
			assertEquals(agent.displayY(), b - 5); 
			assertEquals(agent.getX(), a); 
			assertEquals(agent.getY(), b); 

			assertNull(direction);
			assertEquals(agent.toString(), String.format("(%s, %s) heading null", a, b));
			agent.direction = Direction.right;
			assertEquals(agent.toString(), String.format("(%s, %s) heading right", a, b));
		}

		Agent agent = new Agent(16, 16);
		for (int i=-10; i < 10; i++) {
			Agent.setup(testMap, i);
			if (i==2||i==1) {
				assertTrue(Agent.speed == i);
			} else {
				assertTrue(Agent.speed == 1 || Agent.speed == 2);
			}
		}
	}

	// Testing functions

	private void testValidDirection(Boolean[] expected, Agent agent)
	{
		for (int i=0; i < 4; i++) {
			Direction testDirection = Direction.values()[i];

			boolean truth = agent.validDirection(testDirection) == expected[i];
			if (!truth) {
				System.out.printf("%s should be %s\n",
						testDirection, !agent.validDirection(testDirection)
				);
			}
			assertTrue(truth);
		}
	}

	private void testValidDirections(Boolean[] expected, Agent agent)
	{
		for (int i=0; i < 4; i++) {
			Direction testDirection = Direction.values()[i];
			boolean contained = agent.validDirections().contains(testDirection);

			if (!contained == expected[i]) {
				System.out.printf("%s should be %s\n",
						Arrays.toString(expected), agent.validDirections()
				);
			}
		}
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
}
