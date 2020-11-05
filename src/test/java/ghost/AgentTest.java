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
		Agent.boolMap = testMap;

		lambdaTest<Integer, Integer, Agent.Point> pointCheck
			= (a, b, point) -> assertTrue(point.x == a && point.y == b);

		Agent agent = new Agent(16, 16);

		// test right
		for (int i=0; i < 100; i++) {
			pointCheck.with(16+i, 16, agent.translate(Direction.right, i));
		}

		// test left
		for (int i=0; i < 100; i++) {
			pointCheck.with(16-i, 16, agent.translate(Direction.left, i));
		}

		// test down
		for (int i=0; i < 100; i++) {
			pointCheck.with(16, 16+i, agent.translate(Direction.down, i));
		}

		// test up
		for (int i=0; i < 100; i++) {
			pointCheck.with(16, 16-i, agent.translate(Direction.up, i));
		}
	}

	@Test
	public void currentGridCell()
	{
		Agent.boolMap = testMap;

		lambdaTest<Integer, Integer, Agent.Point> pointCheck
			= (a, b, point) -> assertTrue(point.x == a && point.y == b);
		// This tests validDirection() && validDirections()

		Agent.Point point;

		int a, b;
		int[][] tests = new int[][]{
			{16,  16},
			{32,  16},
			{48,  16},
			{64,  16},
			{80,  16},
			{96,  16},
			{128, 48}
		};

		for (int[] coord : tests) {
			a = coord[0];
			b = coord[1];
			Agent agent = new Agent(a, b);

			// Check left
			point = agent.translate(Direction.left, 1);
			pointCheck.with(a-1, b, point);
			pointCheck.with(a-16, b, Agent.currentGridCell(point, Direction.left));

			// Check right
			point = agent.translate(Direction.right, 1);
			pointCheck.with(a+1, b, point);
			pointCheck.with(a+16, b, Agent.currentGridCell(point, Direction.right));

			// Check up
			point = agent.translate(Direction.up, 1);
			pointCheck.with(a, b-1, point);
			pointCheck.with(a, b-16, Agent.currentGridCell(point, Direction.up));

			// Check down
			point = agent.translate(Direction.down, 1);
			pointCheck.with(a, b+1, point);
			pointCheck.with(a, b+16, Agent.currentGridCell(point, Direction.down));
		}
	}

	@Test
	public void isWall()
	{
		Agent.boolMap = testMap;

		lambdaTest<Direction, Boolean, Agent> checkWall
			= new lambdaTest<Direction, Boolean, Agent>() {
			public void with(Direction d, Boolean bool, Agent agent) {
				Agent.Point point = agent.translate(d, 1);
				point = Agent.currentGridCell(point, d);
				assertTrue(agent.isWall(point) == bool);
			}
		};

		Agent agent;

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
		Agent.boolMap = testMap;

		Agent agent;

		agent = new Agent(16, 16 * 4);
		testValidDirection(new Boolean[]{ false, false, false, false }, agent);

		agent = new Agent(16, 16);
		testValidDirection(new Boolean[]{ false, false, true, false }, agent);

		agent = new Agent(16 * 8, 16 * 3);
		testValidDirection(new Boolean[]{ false, true, false, true }, agent);

		agent = new Agent(16 * 10, 16);
		testValidDirection(new Boolean[]{ false, true, true, true }, agent);

		// TEST FOR X, Y % 16 != 0 (not clearly on grid squares), as such
		// behavuiour should be return no valid neighbours


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

			assertTrue(contained == expected[i]);

			boolean truth = agent.validDirection(testDirection) == expected[i];
			if (!truth) {
				System.out.printf("%s should be %s\n",
						testDirection, !agent.validDirection(testDirection)
				);
			}

			assertTrue(truth);
		}
	}

		/*
		// 6
		player = new Player(8, 3);
		player.setDirection(Direction.down);
		player.move();
		testAllDirections(new Boolean[]{ true, false, false, true }, player);

	/* Visualisation of test map
			0123456789012
		 0*************
		 1*           *
		 2**********  *
		 3***  *   *  *
		 4* **   *    *
		 5*************
	*/

	static boolean[][] testMap = new boolean[][]{
		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false },
		{ false,  true,   true,   true,   true,   true,   true,   true,   true,   true,   true,   true,   false },
		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  true,   true,   false },
		{ false,  false,  false,  true,   true,   false,  true,   true,   true,   false,  true,   true,   false },
		{ false,  true,   false,  false,  true,   true,   true,   false,  true,   true,   true,   true,   false },
		{ false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false,  false }
	};
}
