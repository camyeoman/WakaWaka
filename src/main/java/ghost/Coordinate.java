package ghost;

/**
 * Provides core functionality of an object which has an x and y coordinate,
 * with the required getter methods. Agent and GameObject both extend this.
 */
public class Coordinate {

	/**
	 * The stored x coordinate.
	 */
	protected int x;

	/**
	 * The stored y coordinate.
	 */
	protected int y;

	/**
	 * Initialise a coordinate object with an x and y coordinates.
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns x, but offset by 5. This is only used to align the sprite when
	 * rendering the game.
	 * @return offset x value
	 */
	public int displayX() {
		return (x - 5);
	}

	/**
	 * Returns y, but offset by 5. This is only used to align the sprite when
	 * rendering the game.
	 * @return offset y value
	 */
	public int displayY() {
		return (y - 5);
	}

	/**
	 * Returns unmodified internal x value.
	 * @return x value
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns unmodified internal y value.
	 * @return y value
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns a point with current x and y. Returns a point object with the
	 * x and y attributes set to the current agent's x and y coordinates.
	 * @return point with current x and y
	 */
	public Point point() {
		return new Point(x, y);
	}

}
