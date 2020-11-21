package ghost;

import java.util.List;
import java.util.ArrayList;

public class Agent extends Coordinate {
	protected static Sprite[][] spriteMap;
	protected static int speed = 0;

	protected Direction direction;
	final Point intialPoint;

	/**
	* Initialises an agent with an x and a y coordinate.
	* @param x, the x coordinate
	* @param y, the y coordinate
	*/
	public Agent(int x, int y) {
		super(x, y);

		// Store initial position for soft reset
		this.intialPoint = new Point(x, y);
		this.direction = null;
	}

	/**
	 * Initialises internal static variables.
	 * @param config, a configuration object with the needed data
	 */
	public static void SETUP(Configuration config) {
		Agent.spriteMap = config.spriteMap;

		if (config.speed == 1 || config.speed == 2) {
			Agent.speed = config.speed;
		}
	}

	// Getter and Setter methods

	/**
	 * Resets an agent to initial position and resets direction. This is used
	 * when a player loses a life to a ghost, and this method is partially
	 * overwritten in the sublcasses Player and ghost.
	 */
	public void softReset() {
		this.x = intialPoint.x;
		this.y = intialPoint.y;
		this.direction = null;
	}

	/**
	 * Returns current direction.
	 * @return current direction
	 */
	public Direction direction() {
		return direction;
	}

	// Position and Direction methods

	/**
	 * Return a point object that is the current Agent's position, but translated
	 * by an integer multiple (magnitude) of the speed in a specified direction.
	 * @param direction, the direction to translate the point
	 * @param magnitude, the integer size of the displacement
	 * @return A new Point object with translated coordinates
	 */
	public Point translate(Direction direction, int magnitude) {
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

	// Navigation methods

	/**
	 * Return a whether a specified direction is valid.
	 * @param direction, the direction to verify
	 * @return a boolean representing if the direction is valid
	 */
	public boolean validDirection(Direction newDirection) {
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
	public List<Direction> validDirections() {
		List<Direction> directions = new ArrayList<>();
		
		for (Direction d : Direction.values()) {
			if (validDirection(d)) {
				directions.add(d);
			}
		}

		return directions;
	}

	// Misc methods

	/**
	 * Returns a string containing the coordinates and direction.
	 * @return a string representing the direction and coordinates
	 */
	public String toString() {
		return String.format(
			"(%s, %s) heading %s",
			x,
			y,
			(direction==null ? "null" : direction)
		);
	}
}
