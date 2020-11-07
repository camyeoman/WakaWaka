package ghost;

import processing.core.PImage;
import processing.core.PApplet;
import java.util.*;

public class Player extends Agent {
	private static Map<Direction, PImage> sprites;
	private static PImage closed;

	protected Direction directionQued;
	private boolean open = true;
	int points;

	public Player(int x, int y)
	{
		super(x, y);
		this.directionQued = Direction.right;
	}

	public void tic(PApplet app, int counter)
	{
		if (validDirection(directionQued)) {
			this.direction = this.directionQued;
		}

		if (direction != null && validDirection(direction)) {
			Point point = translate(direction, 1);
			this.x = point.x;
			this.y = point.y;
		}

		app.image(getSprite(counter), displayX(), displayY());
	}

	public void setQuedDirection(Direction newDirection)
	{
		if (newDirection != null) {
			this.directionQued = newDirection;
		}
	}

	public void setDirection(Direction newDirection)
	{

	}

	public PImage getSprite(int counter)
	{
		if (counter % 8 == 0) {
			open = (open) ? false : true;
		}

		if (direction == null) {
			return (open) ? sprites.get(Direction.right) : closed;
		} else {
			return (open) ? sprites.get(direction) : closed;
		}
	}

	public PImage staticSprite()
	{
		return sprites.get(Direction.right);
	}

	public static void setUp(Game game)
	{
		Player.sprites = game.playerSprites;
	}
}
