package ghost;
import processing.core.PImage;
import processing.core.PApplet;
import java.util.regex.Pattern;
import java.util.*;

interface Lambda<T, U> {
	public U eval(T input);
}

public class Agent {

	protected static int speed = 1;
	protected Direction direction;
	final boolean[][] boolMap;
	protected int x, y;

	public float displayX() { return (float)(x - 6); }
	public float displayY() { return (float)(y - 6); }

	public int getX() { return x; }
	public int getY() { return y; }

	public Point getPoint() { return new Point(x, y); }

	public Direction getDirection() { return direction; }

	public int speed() { return speed; }

	public Agent(int x, int y, boolean[][] boolMap)
	{
		// @TEMP figure out exactly what direction later etc.
		this.direction = null;
		this.boolMap = boolMap;
		this.x = x;
		this.y = y;
	}

	// Moving

	public void move()
	{
		if (direction != null && validDirection(direction)) {
			Point point = translate(direction, 1);
			this.x = point.x;
			this.y = point.y;
		}
	}

	protected Point translate(Direction direction, int magnitude)
	{
		// Adds a multiple of the speed to the position,
		// while rounding the position.

		Point point = getPoint();

		switch (direction) {
			case right:  point.x += speed * magnitude;  break;
			case down:   point.y += speed * magnitude;  break;
			case left:   point.x -= speed * magnitude;  break;
			case up:     point.y -= speed * magnitude;  break;
		}

		return point;
	}


	// Interpreting map state

	protected boolean isWall(Point point)
	{
		try {
			return !boolMap[point.y/16][point.x/16];
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}

	protected static Point currentGridCell(Point point, Direction newDirection)
	{
		// get next grid square to check if valid
		if (point.x % 16 != 0 || point.y % 16 != 0) {
			if (newDirection.isHorizontal()) {
				point.x += - (point.x % 16) + (newDirection == Direction.left ? 0 : 16);
			} else {
				point.y += - (point.y % 16) + (newDirection == Direction.up   ? 0 : 16);
			}
		}

		return point;
	}

	// Determining valid actions

	protected boolean validDirection(Direction newDirection)
	{
		if (newDirection == null) {
			return false;
		}

		if (x % 16 == 0 && y % 16 == 0) {
			Point point = translate(newDirection, 1);
			point = currentGridCell(point, newDirection); 
			return !isWall(point);
		} else {
			if (direction == null) {
				return false;
			}
			return this.direction.isHorizontal() == newDirection.isHorizontal();
		}
	}

	protected List<Direction> validDirections()
	{
		List<Direction> directions = new ArrayList<>();
		
		for (Direction d : Direction.values()) {
			if (validDirection(d)) {
				directions.add(d);
			}
		}

		return directions;
	}

	// App related processes

	public PImage getSprite()
	{
		// overwrite in the subclasses
		return null;
	}

	public static boolean setSpeed(int n)
	{
		if (n == 1 || n == 2) {
			speed = n;
			return true;
		}
		return false;
	}

	// Obselete

	public int[] nextCoords(Direction direction, int squares)
	{
		int x = this.x, y = this.y;
		switch (direction) {
			case up:     y -= 16 * squares;  break;
			case left:   x -= 16 * squares;  break;
			case right:  x += 16 * squares;  break;
			case down:   y += 16 * squares;  break;
		}

		return new int[]{ x, y };
	}

	// Misc

	public String toString()
	{
		return "( " + x + ", " + y + " )" + " heading " + (direction==null?"null":direction);
	}

	class Point {
		// Container class for x and y coordinate
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public String toString() { return "( " + x + ", " + y + " )"; }
	}
}
