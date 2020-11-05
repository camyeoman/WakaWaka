package ghost;

import processing.core.PImage;
import processing.core.PApplet;
import java.util.*;

public class Player extends Agent {
	// Sprites
	static Map<Direction, PImage> sprites = new HashMap<>();
	static PImage closed;

	protected Direction directionQued;
	private boolean open = true;
	private int counter = 0;
	int points;

	public Player(int x, int y, boolean[][] map)
	{
		super(x, y, map);
		this.directionQued = Direction.right;
	}

	public void tic(PApplet app, int counter)
	{
		app.image(getSprite(), displayX(), displayY());

		if (direction != null) {
			move();
		}
	}

	public void setDirection(Direction newDirection)
	{
		if (newDirection != null) {
			this.directionQued = newDirection;
		}

		if (validDirection(newDirection)) {
			this.direction = this.directionQued;
		}
	}

	public PImage getSprite()
	{
		if (direction == null) {
			return sprites.get(Direction.right);
		} else {
			if (++counter % 8 == 0) {
				open = (open) ? false : true;
			}

			return (open) ? sprites.get(direction) : closed;
		}
	}
}
