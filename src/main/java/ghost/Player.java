package ghost;

import processing.core.PImage;
import processing.core.PApplet;
import java.util.*;

public class Player extends Agent {
	// Sprites
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

	public static void loadSprites(App app) {
		sprites = new HashMap<>();
		String[] files = new String[]{"Up", "Left", "Right", "Down"};
		for (int i=0; i < 4; i++) {
			PImage sprite = Utilities.pathLoad(app, "player" + files[i]);
			Player.sprites.put(Direction.values()[i], sprite);
		}

		closed = Utilities.pathLoad(app, "playerClosed");
	}

	public void tic(PApplet app, int counter)
	{
		app.image(getSprite(counter), displayX(), displayY());

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

	public PImage scoreboardSprite()
	{
		return sprites.get(Direction.right);
	}
}
