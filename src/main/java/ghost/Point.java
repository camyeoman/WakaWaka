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
		return (int)(Math.pow(xDist + yDist, 0.5));
	}

	public String toString() { return String.format("(%s, %s)", x, y); }
}
