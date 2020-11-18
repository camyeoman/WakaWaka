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
