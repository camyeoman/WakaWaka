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
		switch (this) {
			case left:
				return right;
			case right:
				return left;
			case up:
				return down;
			case down:
				return up;
		}

		return null;
	}

	boolean isHorizontal() {
		if (this == right || this == left) {
			return true;
		} else {
			return false;
		}
	}
}

