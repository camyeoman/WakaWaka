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
		Agent agent = new Agent(1, 1, null);
		assertNotNull(agent);
	}

	@Test
	public void translate()
	{
		lambdaTest<Integer, Integer, Agent.Point> pointCheck
			= (a, b, point) -> assertTrue(point.x == a && point.y == b);

		Agent agent = new Agent(16, 16, testMap);

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
		lambdaTest<Integer, Integer, Agent.Point> pointCheck
			= (a, b, point) -> assertTrue(point.x == a && point.y == b);
		// This tests validDirection() && validDirections()

		Agent.Point point;
		Agent agent = new Agent(16, 16, testMap);

		// Check left
		point = agent.translate(Direction.left, 1);
		pointCheck.with(15, 16, point);
		pointCheck.with(0, 16, Agent.currentGridCell(point, Direction.left));

		// Check right
		point = agent.translate(Direction.right, 1);
		pointCheck.with(17, 16, point);
		pointCheck.with(32, 16, Agent.currentGridCell(point, Direction.right));

		// Check up
		point = agent.translate(Direction.up, 1);
		pointCheck.with(16, 15, point);
		pointCheck.with(16, 0, Agent.currentGridCell(point, Direction.up));

		// Check down
		point = agent.translate(Direction.down, 1);
		pointCheck.with(16, 17, point);
		pointCheck.with(16, 32, Agent.currentGridCell(point, Direction.down));
	}

	@Test
	public void isWall()
	{
		lambdaTest<Direction, Boolean, Agent> checkWall
			= (d, bool, agent) -> {
					assertTrue(
						agent.isWall(Agent.currentGridCell(agent.translate(d, 1),d)) == bool
					);
				};

		Agent agent;

		agent = new Agent(16, 16, testMap);
		checkWall.with(Direction.left, true, agent);
		checkWall.with(Direction.right, false, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, true, agent);

		agent = new Agent(16, 16 * 4, testMap);
		checkWall.with(Direction.left, true, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, true, agent);

		agent = new Agent(16 * 11, 16, testMap);
		checkWall.with(Direction.left, false, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, false, agent);

		agent = new Agent(16 * 11, 16, testMap);
		checkWall.with(Direction.left, false, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, true, agent);
		checkWall.with(Direction.down, false, agent);

		agent = new Agent(16 * 11, 16 * 4, testMap);
		checkWall.with(Direction.left, false, agent);
		checkWall.with(Direction.right, true, agent);
		checkWall.with(Direction.up, false, agent);
		checkWall.with(Direction.down, true, agent);
	}

	@Test
	public void validDirection()
	{
		Agent agent;

		agent = new Agent(16, 16 * 4, testMap);
		testValidDirection(new Boolean[]{ false, false, false, false }, agent);

		agent = new Agent(16, 16, testMap);
		testValidDirection(new Boolean[]{ false, false, true, false }, agent);

		agent = new Agent(16 * 8, 16 * 3, testMap);
		testValidDirection(new Boolean[]{ false, true, false, true }, agent);

		agent = new Agent(16 * 10, 16, testMap);
		testValidDirection(new Boolean[]{ false, true, true, true }, agent);

		// TEST FOR X, Y % 16 != 0 (not clearly on grid squares), as such
		// behavuiour should be return no valid neighbours

		/*

		// 5
		agent = new Agent(16 * 9 + 5, 16, testMap);
		testValidDirection(new Boolean[]{ false, true, true, false }, agent);

		// 6
		agent = new Agent(16 * 8, 16 * 3 + 7, testMap);
		testValidDirection(new Boolean[]{ true, false, false, true }, agent);
		*/
	}

	private void testValidDirection(Boolean[] expected, Agent agent)
	{
		for (int i=0; i < 4; i++) {
			Direction testDirection = Direction.values()[i];

			System.out.println(testDirection);
			System.out.println(agent.validDirection(testDirection));
			boolean truth = agent.validDirection(testDirection) == expected[i];
			if (!truth) {
				System.out.printf("%s should be %s\n",
						testDirection, !agent.validDirection(testDirection)
				);
			}
			// assertTrue(truth);
		}
	}

	private void testValidDirections(Boolean[] expected, Agent agent)
	{
		for (int i=0; i < 4; i++) {
			Direction testDirection = Direction.values()[i];
			boolean contained = agent.validDirections().contains(testDirection);

			if (!contained == expected[i]) {
				System.out.println(
						"expected " + Arrays.toString(expected)
						+ " but got " + agent.validDirections()
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
		testAllDirections(new Boolean[]{ false, false, true, false }, agent);

		player = new Player(1, 4, testMap);
		testAllDirections(new Boolean[]{ false, false, false, false }, player);

		// 3
		player = new Player(8, 3, testMap);
		testAllDirections(new Boolean[]{ false, true, false, true }, player);

		// 4
		player = new Player(10, 1, testMap);
		testAllDirections(new Boolean[]{ false, true, true, true }, player);

		// TEST FOR X, Y % 16 != 0 (not clearly on grid squares), as such
		// behavuiour should be return no valid neighbours

		// 5
		player = new Player(9, 1, testMap);
		player.setDirection(Direction.right);
		player.move();
		testAllDirections(new Boolean[]{ false, true, true, false }, player);

		// 6
		player = new Player(8, 3, testMap);
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
