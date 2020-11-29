package ghost;

/**
 * Container class for x and y coordinate.
 */
public class Point {

	/**
	 * Integer x coordinate.
	 */
	int x;

	/**
	 * Integer x coordinate.
	 */
	int y;

	/**
	 * Create point object with given x and y.
	 * @param x, integer x coordinate
	 * @param y, integer y coordinate
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Return the integer distance between two point objects.
	 * @param point, the point to compare to the current point
	 * @return the integer distance between points
	 */
	public int distance(Point point) {

		int xDist = (int)(Math.pow(this.x - point.x, 2));
		int yDist = (int)(Math.pow(this.y - point.y, 2));
		return (int)(Math.round(Math.pow(xDist + yDist, 0.5)));

	}

	/**
	 * Snap a point to a 16 pixel grid based on the direction of travel. Only
	 * valid if snapping in one direction.
	 * @param direction, the direction of travel
	 * @return the point snapped to the grid
	 */
	public Point gridSnap(Direction direction) {

		int x = this.x, y = this.y;

		if (x % 16 != 0 ^ y % 16 != 0) {
			if (direction.isHorizontal()) {
				x += - (x % 16) + (direction == Direction.left ? 0 : 16);
			} else {
				y += - (y % 16) + (direction == Direction.up   ? 0 : 16);
			}
		}

		return new Point(x, y);

	}

	/**
	 * Restrict the range of a point by provide an upper bound for the
	 * x and y coordinate, with zero being the lower bound.
	 * @param xUpper, x upper bound
	 * @param yUpper, y upper bound
	 * @return point object with restricted range
	 */
	public Point restrictRange(int xUpper, int yUpper) {

		if (x < 0 || x > xUpper) {
			x = (x < 0) ? 0 : xUpper;
		}

		if (y < 0 || y > yUpper) {
			y = (y < 0) ? 0 : yUpper;
		}

		return this;

	}

	/**
	 * Returns if two point objects have the same x,y coordinates.
	 * @param obj, another object to compare
	 * @return if the objects are equal
	 */
	public boolean equals(Object obj) {

		if (obj instanceof Point) {
			Point point = (Point) obj;
			return x == point.x && y == point.y;
		}

		return false;

	}

	/**
	 * String representation of point object, displaying x, y coordates.
	 * @return string representation
	 */
	public String toString() {
		return String.format("(%s, %s)", x, y);
	}

}
