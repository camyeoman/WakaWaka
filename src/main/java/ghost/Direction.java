package ghost;

public enum Direction {

	up(38),
	left(37),
	right(39),
	down(40);
	
	/**
	 * keyCode associated with direction.
	 */
	final int KEY_CODE;

	Direction(int KEY_CODE) {
		this.KEY_CODE = KEY_CODE;
	}

	/**
	 * Get the opposite direction to the current direction.
	 */
	Direction getOpposite() {

		if (this == Direction.left) {
			return right;
		} if (this == Direction.right) {
			return left;
		} if (this == Direction.up) {
			return down;
		} else {
			return up;
		}

	}

	/**
	 * Get the opposite direction to the current direction.
	 */
	boolean isHorizontal() {

		if (this == right || this == left) {
			return true;
		} else {
			return false;
		}

	}

}
