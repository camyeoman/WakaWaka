package ghost;

import java.util.ArrayList;
import java.util.List;

/**
 * Super class to Player and Ghost. Interprets the game map and deals with
 * basic movement.
 */
public class Agent extends Coordinate {

	/**
	 * A map of sprites representing the game map.
	 */
	protected static Sprite[][] SPRITE_MAP;

	/**
	 * An integer representing the speed in range [1,2].
	 */
	protected static int speed = 0;

	/**
	 * Current direction.
	 */
	protected Direction direction;

	/**
	 * Initial position as a point.
	 */
	final Point initialPoint;

	/**
	* Initialises an agent with a given position.
	* @param x, the x coordinate
	* @param y, the y coordinate
	*/
	public Agent(int x, int y) {

		super(x, y);

		// Store initial position for reset
		this.initialPoint = new Point(x, y);
		this.direction = null;

	}

	/**
	 * Initialises internal variables for Agent and its children.
	 * @param config, a configuration object
	 */
	public static void SETUP(Configuration config) {

		Agent.SPRITE_MAP = config.spriteMap;
		Ghost.setChaser(null);

		if (config.speed == 1 || config.speed == 2) {
			Agent.speed = config.speed;
		}

	}

	// Getter and Setter methods

	/**
	 * Returns current direction.
	 * @return current direction
	 */
	public Direction direction() {
		return direction;
	}

	/**
	 * Resets an agent to initial position and resets direction. This is used
	 * when a player loses a life to a ghost, and this method is partially
	 * overwritten in the sublcasses Player and ghost.
	 */
	public void reset() {

		this.x = initialPoint.x;
		this.y = initialPoint.y;
		this.direction = null;

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
				case right:
					point.x += speed * magnitude;
					break;
				case down:
					point.y += speed * magnitude;
					break;
				case left:
					point.x -= speed * magnitude;
					break;
				case up:
					point.y -= speed * magnitude;
					break;
			}
		}

		return point;

	}

	// Navigation methods

	/**
	 * Return a whether a specified direction is valid.
	 * @param newDirection, the direction to verify
	 * @return a boolean representing if the direction is valid
	 */
	public boolean validDirection(Direction newDirection) {

		if (newDirection == null) {
			return false;
		}

		if (x % 16 == 0 && y % 16 == 0) {
			Point point = translate(newDirection, 1).gridSnap(newDirection);

			try {
				Sprite sprite = SPRITE_MAP[point.y/16][point.x/16];
				return Sprite.occupiable(sprite);
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
	 * @return a list of valid directions
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

	/**
	 * Returns a string displaying the coordinates and direction.
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
