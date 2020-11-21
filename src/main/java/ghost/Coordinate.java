package ghost;

public class Coordinate {
	protected int x, y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns x, but offset by 5 and cast to a float. This is only used to
	 * align the sprite when rendering the game.
	 * @return offset x value
	 */
	public float displayX() {
		return (float)(x - 5);
	}

	/**
	 * Returns y, but offset by 5 and cast to a float. This is only used to
	 * align the sprite when rendering the game.
	 * @return offset y value
	 */
	public float displayY() {
		return (float)(y - 5);
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
