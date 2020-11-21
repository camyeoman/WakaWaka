package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

interface lambdaTest<T, U, V> {
	public void with(T a, U b, V c);
}

public class AgentTest extends TestTools {
	static {
		Configuration config = new Configuration();
		config.spriteMap = testMap;
		config.speed = 1;

		Agent.SETUP(config);
	}

	@Test
	public void constructor()
	{
		Agent agent = new Agent(1, 1);
		assertNotNull(agent);
		assertNull(agent.direction());
		assertEquals(agent.intialPoint, new Point(1, 1));
		assertEquals(agent.point(), new Point(1, 1));
	}

	@Test
	public void translate() {
		Point[] points = new Point[]{
			new Point(16,  16), new Point(32,  16), new Point(48,  16),
			new Point(64,  16), new Point(80,  16), new Point(96,  16)
		};

		for (Point point : points) {
			Agent agent = new Agent(point.x, point.y);

			for (int i=-20; i < 20; i++) {
				assertEquals(new Point(point.x+i, point.y), agent.translate(Direction.right, i));
				assertEquals(new Point(point.x-i, point.y), agent.translate(Direction.left, i));
				assertEquals(new Point(point.x, point.y+i), agent.translate(Direction.down, i));
				assertEquals(new Point(point.x, point.y-i), agent.translate(Direction.up, i));

				// null case
				assertEquals(new Point(point.x, point.y), agent.translate(null, i));
			}
		}
	}

	@Test
	public void softReset() {
		Point[] points = new Point[]{
			new Point(16,  16), new Point(32,  16), new Point(48,  16),
			new Point(64,  16), new Point(80,  16), new Point(96,  16)
		};

		for (Point point : points) {
			Agent agent = new Agent(point.x, point.y);

			for (int i=-20; i < 20; i++) {
				Point newPoint = agent.translate(Direction.right, i);
				agent.direction = Direction.values()[Math.abs(i % 4)];
				agent.x = newPoint.x;
				agent.y = newPoint.y;

				agent.softReset();
				assertEquals(agent.intialPoint, agent.point());
				assertEquals(agent.intialPoint, point);
			}
		}
	}

	public void SETUP() {
		Configuration config = new Configuration();
		config.speed = 5;
	}

	@Test
	public void validDirection()
	{
		Agent agent;

		// test behaviour on grid squares, i.e % 16 == 0
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

		// Test out of bounds
		agent = new Agent(1600, 16);
		agent.direction = Direction.right;
		testValidDirection(new Boolean[]{ false, false, false, false }, agent);
	}

	@Test
	public void validDirections()
	{
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

	/*
	@Test
	public void getterAndSetterMethods()
	{
		Point[] points = new Point[]{
			new Point(16,  16), new Point(32,  16), new Point(48,  16),
			new Point(64,  16), new Point(80,  16), new Point(96,  16)
		};

		for (Point point : points) {
			Agent agent = new Agent(point.x, point.y);
			Direction direction = agent.direction();

			assertEquals(agent.displayX(), a - 5); 
			assertEquals(agent.displayY(), b - 5); 
			assertEquals(agent.getX(), a); 
			assertEquals(agent.getY(), b); 

			assertNull(direction);
			assertEquals(agent.toString(), String.format("(%s, %s) heading null", a, b));
			agent.direction = Direction.right;
			assertEquals(agent.toString(), String.format("(%s, %s) heading right", a, b));
		}

	}

		Agent agent = new Agent(16, 16);
		for (int i=-10; i < 10; i++) {
			if (i==2||i==1) {
				assertTrue(Agent.speed == i);
			} else {
				assertTrue(Agent.speed == 1 || Agent.speed == 2);
			}
		}
	*/

	// Testing functions

	private void testValidDirection(Boolean[] expected, Agent agent) {
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

	private void testValidDirections(Boolean[] expected, Agent agent) {
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

}
