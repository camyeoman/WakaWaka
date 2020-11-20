package ghost;
import processing.core.PApplet;
import java.util.regex.Pattern;
import java.util.*;
import java.util.stream.*;

interface Lambda<T, U> {
	public U eval(T input);
}

public class Agent {
	protected static Sprite[][] spriteMap;
	protected static int speed = 0;

	protected Direction direction;
	protected int x, y;

	final Point intialPoint;

	public Agent(int x, int y)
	{
		// Store initial position for soft reset
		this.intialPoint = new Point(x, y);

		this.direction = null;
		this.x = x;
		this.y = y;
	}

	public void softReset()
	{
		this.x = intialPoint.x;
		this.y = intialPoint.y;
		this.direction = null;
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

	public Point point()
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
	 * @return A new Point object with translated coordinates
	 */
	public Point translate(Direction direction, int magnitude)
	{
		Point point = point();

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

	// Navigation

	/**
	 * Return a whether a specified direction is valid.
	 * @param direction, the direction to verify
	 * @return a boolean representing if the direction is valid
	 */
	public boolean validDirection(Direction newDirection)
	{
		if (newDirection == null) {
			return false;
		}

		if (x % 16 == 0 && y % 16 == 0) {
			Point point = translate(newDirection, 1).gridSnap(newDirection);

			try {
				Sprite sprite = spriteMap[point.y/16][point.x/16];
				return !sprite.isWall();
			} catch (IndexOutOfBoundsException e) {
				return false;
			}

		} else {

			if (direction == null) {
				return false;
			} else {
				return this.direction.isHorizontal() == newDirection.isHorizontal();
			}

		}
	}

	/**
	 * Returns a list of all valid directions.
	 * @return the list of valid directions
	 */
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

	/**
	 * Initialises internal static variables.
	 * @param map, the game map, made of Sprite objects
	 * @param speed, the speed, can either be 1 or 2
	 */
	public static void SETUP(Sprite[][] map, int speed)
	{
		Agent.spriteMap = map;

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
