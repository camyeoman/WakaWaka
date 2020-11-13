package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class GameTest {
	/* Visualisation of test map
			0123456789012
		 0*************
		 1*C        B *
		 2**********  *
		 3***  *  A*  *
		 4*D**   *    *
		 5*************
	*/

	@Test
	public void constructor()
	{
		App app = new App();
		assertNotNull(app.game);
		assertNotNull(app.game.app);
	}

	@Test
	public void setUp()
	{
		App app = new App();
		System.out.println(app.game);
		Game game = app.game;
		assertTrue(game.initLives == game.lives);
	}

}
