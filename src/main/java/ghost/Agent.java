package ghost;
import processing.core.PImage;
import processing.core.PApplet;
import java.util.regex.Pattern;
import java.util.*;

public class Agent {
	protected static double speed;
	protected Direction direction;
	final boolean[][] boolMap;
	protected double x, y;

	public float displayX() { return (float)(x - 6); }
	public float displayY() { return (float)(y - 6); }

	public double getX() { return x; }
	public double getY() { return y; }
	public double[] getCoords() { return new double[]{x,y}; }

	public Direction getDirection() { return direction; }

	public void setSpeed(double speed) { this.speed = speed; }
	public double speed() { return speed; }

	public Agent(double x, double y, boolean[][] boolMap)
	{
		// @TEMP figure out exactly what direction later etc.
		this.direction = null;
		this.boolMap = boolMap;
		this.x = 16 * x;
		this.y = 16 * y;
	}

	public static boolean setSpeed(int n) {
		if (n == 1 || n == 2) {
			speed = 0.8 * n;
			return true;
		}
		return false;
	}

	// Moving

	public void setDirection(Direction newDirection)
	{
		if (validDirection(newDirection)) {
			this.direction = newDirection;
		}
	}

	public void move()
	{
		if (direction != null && validDirection(direction)) {
			switch (direction) {
				case right:  translate(1, 0);  break;
				case down:   translate(0, 1);  break;
				case left:   translate(-1,0);  break;
				case up:     translate(0,-1);  break;
			}
		}
	}

	protected void translate(int x, int y)
	{
		// Adds a multiple of the speed to the position,
		// while rounding the position.

		this.x = Utilities.round(this.x + speed * x, 2);
		this.y = Utilities.round(this.y + speed * y, 2);
	}

	// Navigation

	public double[] nextCoords(Direction direction, double squares)
	{
		double x = this.x, y = this.y;
		if (x % 16 != 0 || y % 16 != 0) {
			if (direction.isHorizontal()) {
				x += - (x % 16) + (direction == Direction.left ? 0 : 16);
			} else {
				y += - (y % 16) + (direction == Direction.down ? 0 : 16);
			}
		} if (!(x % 16 != 0 || y % 16 != 0) || squares > 1) {
			switch (direction) {
				case up:     y -= 16 * squares;  break;
				case left:   x -= 16 * squares;  break;
				case right:  x += 16 * squares;  break;
				case down:   y += 16 * squares;  break;
			}
		}

		return new double[]{ x, y };
	}

	protected boolean validDirection(Direction newDirection)
	{
		double x = this.x, y = this.y;

		if (x % 16 == 0 && y % 16 == 0) {
			double[] p = nextCoords(newDirection, 1);
			try {
				return boolMap[(int)p[1]/16][(int)p[0]/16];
			} catch (IndexOutOfBoundsException e) {
				return false;
			}
		} else {
			return this.direction.isHorizontal() == newDirection.isHorizontal();
		}
	}

	protected List<Direction> validDirections()
	{
		List<Direction> directions = new ArrayList<>();
		
		for (Direction d : Direction.values()) {
			if (validDirection(d)) {
				directions.add(d);
			}
		}

		return directions;
	}

	public PImage getSprite() {
		// overwrite in the subclasses
		return null;
	}

	// Misc

	public String toString()
	{
		return "( " + x + ", " + y + " )" + " heading " + direction;
	}
}
