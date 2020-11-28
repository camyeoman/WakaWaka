package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class PointTest {

	/**
	 * Test point constructor with given x and y.
	 */
	@Test
	public void constructor() {

		Point point = new Point(1, 1);
		assertNotNull(point);
		assertTrue(point.x == 1 && point.y == 1);

	}

	/**
	 * Test getting the distance between two point objects.
	 */
	@Test
	public void distance() {

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

	/**
	 * Test restricting the range of a point with upper bounds.
	 */
	@Test
	public void restrictRange() {

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

		// Extra

		point = new Point (224, 160).restrictRange(448, 576);
		assertTrue(point.x == 224 && point.y == 160);

	}

	/**
	 * Test the string representation of the state of a point.
	 */
	@Test
	public void testToString() {

		Point point = new Point(1, 1);
		assertEquals(point.toString(), "(1, 1)");

	}


	/**
	 * Test snapping to a 16 pixel grid squares.
	 */
	@Test
	public void gridSnap() {

		Point point, testPoint;

		point = new Point(16 + 5, 16);
		assertEquals(point.gridSnap(Direction.left), new Point(16, 16));
		assertEquals(point.gridSnap(Direction.right), new Point(32, 16));

		point = new Point(16, 16 - 5);
		assertEquals(point.gridSnap(Direction.up), new Point(16, 0));
		assertEquals(point.gridSnap(Direction.down), new Point(16, 16));

		point = new Point(16, 16);

		for (Direction d : Direction.values()) {
			assertEquals(point, point.gridSnap(d));
		}

	}

	/**
	 * Test the equals method.
	 */
	@Test
	public void equals() {

		Point[] pointsA = new Point[]{
			new Point(16,  16), new Point(32,  16), new Point(48,  16),
			new Point(64,  16), new Point(80,  16), new Point(96,  16)
		};

		Point[] pointsB = new Point[]{
			new Point(16,  16), new Point(32,  16), new Point(48,  16),
			new Point(64,  16), new Point(80,  16), new Point(96,  16)
		};

		Point[] pointsC = new Point[]{
			new Point(17,  15), new Point(33,  17), new Point(49,  17),
			new Point(65,  17), new Point(81,  17), new Point(97,  17)
		};

		for (int i=0; i < pointsA.length; i++) {

			// Equal points
			assertEquals(pointsA[i], pointsB[i]);

			// Not equal points
			assertNotEquals(pointsA[i], pointsC[i]);

		}

		// Different type
		assertNotEquals(new Point(1, 1), "hello");

	}

}
