package ghost;

public enum Direction {
	up(38),
	left(37),
	right(39),
	down(40);
	
	final int KEY_CODE;

	Direction(int KEY_CODE) {
		this.KEY_CODE = KEY_CODE;
	}

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

	boolean isHorizontal() {
		if (this == right || this == left) {
			return true;
		} else {
			return false;
		}
	}
}

