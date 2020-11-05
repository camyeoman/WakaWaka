package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GhostTest {
	/* Visualisation of test map
			0123456789012
		 0*************
		 1*          g*
		 2**********  *
		 3***  *   *  *
		 4* **   *   p*
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
		/*
		Ghost ghost = new Ghost(1, 1);
		assertNotNull(ghost);
		*/
	}

	private void testAllDirections(Boolean[] expected, Ghost ghost) {
		for (int i=0; i < 4; i++) {
			Direction testDirection = Direction.values()[i];
			boolean contained = ghost.validDirections().contains(testDirection);

			assertTrue(contained == expected[i]);
			assertTrue(ghost.validDirection(testDirection) == expected[i]);
		}
	}

	@Test
	public void validDirection_and_validDirections()
	{
		/*
		// This tests validDirection() && validDirections()

		// test return values for (in this order); up, left, right, down
		int testCounter = 1;

		// TEST FOR X, Y % 16 == 0 (clearly on grid squares)

		//System.out.println(ghost.validDirections());
		// 1
		Ghost ghost = new Ghost(1, 1, );
		testAllDirections(new Boolean[]{ false, false, true, false }, ghost);
		//                                 up    left  right  down

		// 2
		ghost = new Ghost(1, 4, );
		testAllDirections(new Boolean[]{ false, false, false, false }, ghost);

		// 3
		ghost = new Ghost(8, 3, );
		testAllDirections(new Boolean[]{ false, true, false, true }, ghost);

		// 4
		ghost = new Ghost(10, 1, );
		testAllDirections(new Boolean[]{ false, true, true, true }, ghost);

		// TEST FOR X, Y % 16 != 0 (not clearly on grid squares), as such
		// behavuiour should be return no valid neighbours

		// 5
		ghost = new Ghost(9, 1, );
		ghost.setDirection(Direction.right);
		ghost.move();
		testAllDirections(new Boolean[]{ false, true, true, false }, ghost);

		// 6
		ghost = new Ghost(8, 3, );
		ghost.setDirection(Direction.down);
		ghost.move();
		testAllDirections(new Boolean[]{ true, false, false, true }, ghost);
		*/
	}

	@Test
	public void move()
	{
		/*
		Ghost ghost = new Ghost(10, 1, );
		// test going right
		double vel = ghost.speed();
		ghost.setDirection(Direction.right);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(ghost.getX() - (160 + vel * i)) < 0.0001 );
			ghost.move();
		}

		// test going left
		ghost.setDirection(Direction.left);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(ghost.getX() - (176 - vel * i)) < 0.0001 );
			ghost.move();
		}

		// test going down
		ghost.setDirection(Direction.down);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(ghost.getY() - (16 + vel * i)) < 0.0001 );
			ghost.move();
		}

		// test going up
		ghost.setDirection(Direction.up);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(ghost.getY() - (32 - vel * i)) < 0.0001 );
			ghost.move();
		}
		*/
	}
}
