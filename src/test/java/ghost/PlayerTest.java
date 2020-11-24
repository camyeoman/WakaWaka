package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class PlayerTest extends TestTools {
	/* Visualisation of test map
			0123456789012
		 0*************
		 1*           *
		 2**********  *
		 3***  *   *  *
		 4* **   *    *
		 5*************
	*/
	@Test
	public void constructor()
	{
		Player player = new Player(1, 1);
		assertNotNull(player);
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
