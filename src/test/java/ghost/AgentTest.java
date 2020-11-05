package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

interface lambdaTest<T, U> {
	public void with(T a, U b);
}


public class AgentTest {
	/* Visualisation of test map
			0123456789012
		 0*************
		 1*           *
		 2**********  *
		 3***  *  p*  *
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
		
	@Test
	public void constructor() {
		Agent agent = new Agent(1, 1, null);
		assertNotNull(agent);
	}

	@Test
	public void validDirection_and_validDirections()
	{
		/*
		// This tests validDirection() && validDirections()

		// test return values for (in this order); up, left, right, down
		lambdaTest<Boolean[], Agent> testAllDirections = (expected, testAgent) -> {
			for (int i=0; i < 4; i++) {
				Direction testDirection = Direction.values()[i];
				boolean contained = testAgent.validDirections().contains(testDirection);
				assertTrue(contained == expected[i]);
				assertTrue(testAgent.validDirection(testDirection) == expected[i]);
			}
		};

		int testCounter = 1;

		// TEST FOR X, Y % 16 == 0 (clearly on grid squares)

		// 1
		Agent agent = new Agent(1, 1, testMap);
		testAllDirections.with(new Boolean[]{ false, false, true, false }, agent);
		//                                      up    left  right  down

		// 2
		agent = new Agent(1, 4, testMap);
		testAllDirections.with(new Boolean[]{ false, false, false, false }, agent);

		// 3
		agent = new Agent(8, 3, testMap);
		testAllDirections.with(new Boolean[]{ false, true, false, true }, agent);

		// 4
		agent = new Agent(10, 1, testMap);
		testAllDirections.with(new Boolean[]{ false, true, true, true }, agent);

		// TEST FOR X, Y % 16 != 0 (not clearly on grid squares), as such
		// behavuiour should be return no valid neighbours

		// 5
		agent = new Agent(9, 1, testMap);
		agent.setDirection(Direction.right);
		agent.move();
		testAllDirections.with(new Boolean[]{ false, true, true, false }, agent);

		// 6
		agent = new Agent(8, 3, testMap);
		agent.setDirection(Direction.down);
		agent.move();
		testAllDirections.with(new Boolean[]{ true, false, false, true }, agent);
		*/
	}

	@Test
	public void move()
	{
		/*
		Agent agent = new Agent(10, 1, testMap);
		// test going right
		double vel = agent.velocity();
		agent.setDirection(Direction.right);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(agent.getX() - (160 + vel * i)) < 0.0001 );
			agent.move();
		}

		// test going left
		agent.setDirection(Direction.left);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(agent.getX() - (176 - vel * i)) < 0.0001 );
			agent.move();
		}

		// test going down
		agent.setDirection(Direction.down);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(agent.getY() - (16 + vel * i)) < 0.0001 );
			agent.move();
		}

		// test going up
		agent.setDirection(Direction.up);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(agent.getY() - (32 - vel * i)) < 0.0001 );
			agent.move();
		}
		*/
	}
}
