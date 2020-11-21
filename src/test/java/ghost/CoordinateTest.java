package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class CoordinateTest {
	@Test
	public void constructor()
	{
		Point[] points = new Point[]{
			new Point(16,  16), new Point(32,  16), new Point(48,  16),
			new Point(64,  16), new Point(80,  16), new Point(96,  16)
		};

		for (Point point : points) {
			Coordinate coord = new Coordinate(point.x, point.y);

			assertNotNull(coord);
			assertTrue(coord.x == point.x && coord.y == point.y);
		}
	}

	@Test
	public void getterMethods() {
		Point[] points = new Point[]{
			new Point(16,  16), new Point(32,  16), new Point(48,  16),
			new Point(64,  16), new Point(80,  16), new Point(96,  16)
		};

		for (Point point : points) {
			Coordinate coord = new Coordinate(point.x, point.y);

			assertTrue(coord.displayX() == (float) coord.x - 5);
			assertTrue(coord.displayY() == (float) coord.y - 5);

			assertTrue(coord.getX() == coord.x);
			assertTrue(coord.getY() == coord.y);

			assertEquals(coord.point(), new Point(point.x, point.y));
		}
	}
}
