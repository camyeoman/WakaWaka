package ghost;
import processing.core.PImage;
import processing.core.PApplet;
import java.util.regex.Pattern;
import java.util.*;

interface Lambda<T, U> {
	public U eval(T input);
}

public class Agent {
	protected static boolean[][] boolMap;
	protected static int speed = 1;
	protected Direction direction;
	protected int x, y;

	public float displayX() { return (float)(x - 5); }
	public float displayY() { return (float)(y - 5); }

	public int getX() { return x; }
	public int getY() { return y; }

	public Point getPoint() { return new Point(x, y); }

	public Direction getDirection() { return direction; }

	public Agent(int x, int y)
	{
		this.direction = null;
		this.x = x;
		this.y = y;
	}

	// Position and Direction

	public Point translate(Direction direction, int magnitude)
	{
		// Adds an interger multiple of the speed to the existing
		// position, and creates a new Point object with the new
		// coordinates.

		Point point = getPoint();

		if (direction != null) {
			switch (direction) {
				case right:  point.x += speed * magnitude;  break;
				case down:   point.y += speed * magnitude;  break;
				case left:   point.x -= speed * magnitude;  break;
				case up:     point.y -= speed * magnitude;  break;
			}
		}

		return point;
	}

	protected void moveTo(Point point)
	{
		this.x = point.x;
		this.y = point.y;
	}

	// Interpreting map state

	public boolean isWall(Point point)
	{
		try {
			return !boolMap[point.y/16][point.x/16];
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}

	public static Point currentGridCell(Point point, Direction newDirection)
	{
		// get next grid square to check if valid
		if (point.x % 16 != 0 ^ point.y % 16 != 0) {
			if (newDirection.isHorizontal()) {
				point.x += - (point.x % 16) + (newDirection == Direction.left ? 0 : 16);
			} else {
				point.y += - (point.y % 16) + (newDirection == Direction.up   ? 0 : 16);
			}
		}

		return point;
	}

	// Determining valid actions

	public boolean validDirection(Direction newDirection)
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

	public List<Direction> validDirections()
	{
		List<Direction> directions = new ArrayList<>();
		
		for (Direction d : Direction.values()) {
			if (validDirection(d)) {
				directions.add(d);
			}
		}

		return directions;
	}

	public static void loadConfig(boolean[][] boolMap, int speed)
	{
		List<Point> corners = new ArrayList<>();
		Agent.boolMap = boolMap;

		setSpeed(speed);
		// Comparator.comparing(Employee::getAge).thenComparing(Employee::getName)
	}

	// App related processes

	public static boolean setSpeed(int n)
	{
		if (n == 1 || n == 2) {
			speed = n;
			return true;
		}
		return false;
	}

	public static void setUp(Game game)
	{
		Agent.boolMap = game.boolMap;
		Agent.speed = game.speed;
	}

	// Misc

	public String toString()
	{
		return String.format("(%s, %s) heading %s", x, y,
			(direction==null ? "null" : direction));
	}
}
