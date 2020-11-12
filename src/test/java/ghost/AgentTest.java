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
		Agent.setUp(testMap, 1);

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
	public void currentGridCell()
	{
		Agent.setUp(testMap, 1);

		lambdaTest<Integer, Integer, Point> pointCheck
			= (a, b, point) -> assertTrue(point.x == a && point.y == b);
		// This tests validDirection() && validDirections()

		Point point;

		int a, b;
		int[][] tests = new int[][]{
			{16,  16}, {32,  16}, {48,  16},
			{64,  16}, {80,  16}, {96,  16}
		};

		for (int magnitude=1; magnitude < 10; magnitude++) {
			for (int[] coord : tests) {
				a = coord[0];
				b = coord[1];
				Agent agent = new Agent(a, b);

				// Check left
				point = agent.translate(Direction.left, magnitude);
				pointCheck.with(a-magnitude, b, point);
				pointCheck.with(a-16, b, Agent.currentGridCell(point, Direction.left));

				// Check right
				point = agent.translate(Direction.right, magnitude);
				pointCheck.with(a+magnitude, b, point);
				pointCheck.with(a+16, b, Agent.currentGridCell(point, Direction.right));

				// Check up
				point = agent.translate(Direction.up, magnitude);
				pointCheck.with(a, b-magnitude, point);
				pointCheck.with(a, b-16, Agent.currentGridCell(point, Direction.up));

				// Check down
				point = agent.translate(Direction.down, magnitude);
				pointCheck.with(a, b+magnitude, point);
				pointCheck.with(a, b+16, Agent.currentGridCell(point, Direction.down));
			}
		}

		// test on grid squares
		for (int[] coord : tests) {
			int x = coord[0], y = coord[1];
			point = (new Agent(x, y)).getPoint();
			point = Agent.currentGridCell(point, Direction.right);
			assertTrue(point.x == x && point.y == y);
		}
	}

	@Test
	public void isWall()
	{
		Agent.setUp(testMap, 1);

		lambdaTest<Direction, Boolean, Agent> checkWall
			= new lambdaTest<Direction, Boolean, Agent>() {
			public void with(Direction d, Boolean bool, Agent agent) {
				Point point = agent.translate(d, 1);
				point = Agent.currentGridCell(point, d);
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
		Agent.setUp(testMap, 1);

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
		Agent.setUp(testMap, 1);

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

	@Test
	public void getterAndSetterMethods()
	{
		Agent.setUp(testMap, 1);

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
			Agent.setUp(testMap, i);
			if (i==2||i==1) {
				assertTrue(Agent.speed == i);
			} else {
				assertTrue(Agent.speed == 1 || Agent.speed == 2);
			}
		}
	}

	@Test
	public void pointClass()
	{
		Agent.setUp(testMap, 1);

		Agent agent = new Agent(16, 16);
		Point point = agent.getPoint();
		assertTrue(point != null && point.x == 16 && point.y == 16);
		assertEquals("(16, 16)", point.toString());
	}

	static boolean[][] testMap = new boolean[][]
	{
		/* Visualisation of test map
				0123456789012
			 0*************
			 1*           *
			 2**********  *
			 3***  *   *  *
			 4* **   *    *
			 5*************
		*/

		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false },
		{ false,  true,   true,   true,   true,   true,   true,   true,   true,   true,   true,   true,   false },
		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  true,   true,   false },
		{ false,  false,  false,  true,   true,   false,  true,   true,   true,   false,  true,   true,   false },
		{ false,  true,   false,  false,  true,   true,   true,   false,  true,   true,   true,   true,   false },
		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false }
	};
}
