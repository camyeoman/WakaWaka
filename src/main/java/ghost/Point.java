package ghost;

public class Point {
	// Container class for x and y coordinate
	int x, y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int distance(Point point)
	{
		int xDist = (int)(Math.pow(this.x - point.x, 2));
		int yDist = (int)(Math.pow(this.y - point.y, 2));
		return (int)(Math.round(Math.pow(xDist + yDist, 0.5)));
	}

	public Point gridSnap(Direction direction)
	{
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

	public Point restrictRange(int xUpper, int yUpper)
	{
		if (x < 0 || x > xUpper) {
			x = (x < 0) ? 0 : xUpper;
		}

		if (y < 0 || y > yUpper) {
			y = (y < 0) ? 0 : yUpper;
		}

		return this;
	}

	public boolean equals(Point point)
	{
		return x == point.x && y == point.y;
	}

	public String toString() { return String.format("(%s, %s)", x, y); }
}
