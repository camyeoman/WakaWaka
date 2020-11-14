package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class PointTest {
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
		Point point = new Point(1, 1);
		assertNotNull(point);
		assertTrue(point.x == 1 && point.y == 1);
	}

	@Test
	public void distance()
	{
		Point a, b;

		// Horizontal
		a = new Point(5, 0);
		b = new Point(0, 0);
		assertTrue(a.distance(b) == 5);

		// Vertical
		a = new Point(0, 5);
		b = new Point(0, 0);
		assertTrue(a.distance(b) == 5);

		// Diagonal
		a = new Point(5, 5);
		b = new Point(0, 2);
		assertTrue(a.distance(b) == 6);
	}

	@Test
	public void restrictRange()
	{
		Point point;

		// Horizontal
		point = new Point(-30, 10).restrictRange(50, 50);
		assertTrue(point.x == 0 && point.y == 10);

		point = new Point(60, 10).restrictRange(50, 50);
		assertTrue(point.x == 50 && point.y == 10);

		point = new Point(10, 10).restrictRange(50, 50);
		assertTrue(point.x == 10 && point.y == 10);

		// Vertical
		point = new Point(10, -30).restrictRange(50, 50);
		assertTrue(point.x == 10 && point.y == 0);

		point = new Point(10, 60).restrictRange(50, 50);
		assertTrue(point.x == 10 && point.y == 50);

		point = new Point(10, 10).restrictRange(50, 50);
		assertTrue(point.x == 10 && point.y == 10);

	}

	@Test
	public void testToString()
	{
		Point point = new Point(1, 1);
		assertEquals(point.toString(), "(1, 1)");
	}
}
