package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class PlayerTest {
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
		
	@Test
	public void constructor()
	{
		Player player = new Player(1, 1);
		assertNotNull(player);
	}

	private void testAllDirections(Boolean[] expected, Player player)
	{
		for (int i=0; i < 4; i++) {
			Direction testDirection = Direction.values()[i];
			boolean contained = player.validDirections().contains(testDirection);

			assertTrue(contained == expected[i]);
			assertTrue(player.validDirection(testDirection) == expected[i]);
		}
	}

	@Test
	public void getSprite()
	{
		int counter = 0;
		Player player = new Player(16, 16);
		for (Direction d : Direction.values()) {
			player.direction = d;
			counter = 8;
			//assertEquals(player.getSprite(counter++), player.sprites.get(d));
			//assertEquals(player.getSprite(counter), player.sprites.get(d));
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

		// 1
		Player player = new Player(1, 1, );
		testAllDirections(new Boolean[]{ false, false, true, false }, player);
		//                                 up    left  right  down

		// 2
		player = new Player(1, 4, );
		testAllDirections(new Boolean[]{ false, false, false, false }, player);

		// 3
		player = new Player(8, 3, );
		testAllDirections(new Boolean[]{ false, true, false, true }, player);

		// 4
		player = new Player(10, 1, );
		testAllDirections(new Boolean[]{ false, true, true, true }, player);

		// TEST FOR X, Y % 16 != 0 (not clearly on grid squares), as such
		// behavuiour should be return no valid neighbours

		// 5
		player = new Player(9, 1, );
		player.setDirection(Direction.right);
		player.move();
		testAllDirections(new Boolean[]{ false, true, true, false }, player);

		// 6
		player = new Player(8, 3, );
		player.setDirection(Direction.down);
		player.move();
		testAllDirections(new Boolean[]{ true, false, false, true }, player);
		*/
	}

	@Test
	public void move()
	{
		/*
		Player player = new Player(10, 1, );
		// test going right
		int vel = player.speed();
		player.setDirection(Direction.right);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(player.getX() - (160 + vel * i)) < 0.0001 );
			player.move();
		}

		// test going left
		player.setDirection(Direction.left);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(player.getX() - (176 - vel * i)) < 0.0001 );
			player.move();
		}

		// test going down
		player.setDirection(Direction.down);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(player.getY() - (16 + vel * i)) < 0.0001 );
			player.move();
		}

		// test going up
		player.setDirection(Direction.up);
		for (int i=0; i < (int)(16/vel); i++) {
			assertTrue( Math.abs(player.getY() - (32 - vel * i)) < 0.0001 );
			player.move();
		}
		*/
	}
}
