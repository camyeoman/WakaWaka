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

	public String toString() { return String.format("(%s, %s)", x, y); }
}
