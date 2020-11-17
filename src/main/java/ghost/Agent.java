package ghost;
import processing.core.PApplet;
import java.util.regex.Pattern;
import java.util.*;
import java.util.stream.*;

interface Lambda<T, U> {
	public U eval(T input);
}

public class Agent {
	protected static Boolean[][] boolMap;
	protected static int speed = 0;

	protected boolean alive = true;
	protected Direction direction;
	protected int x, y;

	public Agent(int x, int y)
	{
		this.direction = null;
		this.x = x;
		this.y = y;
	}

	public float displayX()
	{
		return (float)(x - 5);
	}

	public float displayY()
	{
		return (float)(y - 5);
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Point getPoint()
	{
		return new Point(x, y);
	}

	public Direction getDirection()
	{
		return direction;
	}

	// Position and Direction

	/**
	 * Return a point object that is the current Agent's position, but translated
	 * by an integer multiple (magnitude) of the speed in a specified direction.
	 * @param direction, the direction to translate the point
	 * @param magnitude, the integer size of the displacement
	 * @return A new Point object with translated coordinates.
	 */
	public Point translate(Direction direction, int magnitude)
	{
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

	// Interpreting map state

	public boolean isWall(Point point)
	{
		try {
			return !boolMap[point.y/16][point.x/16];
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}

	// Determining valid actions

	public boolean validDirection(Direction newDirection)
	{
		if (newDirection == null) {
			return false;
		}

		if (x % 16 == 0 && y % 16 == 0) {
			Point point = translate(newDirection, 1).gridSnap(newDirection);
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

	// App related processes

	public static void setup(Sprite[][] map, int speed)
	{
		boolMap = new Boolean[36][28];

		for (int i=0; i < 36; i++) {
			boolMap[i] = Arrays.stream(map[i])
										.map(c -> c == null)
										.toArray(Boolean[]::new);
		}

		//boolMap = Arrays.stream(map);
		if (speed == 1 || speed == 2) {
			Agent.speed = speed;
		}

	}

	// Misc

	public String toString()
	{
		return String.format(
			"(%s, %s) heading %s",
			x,
			y,
			(direction==null ? "null" : direction)
		);
	}
}
