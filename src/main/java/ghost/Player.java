package ghost;

import processing.core.PImage;
import processing.core.PApplet;
import java.util.*;

public class Player extends Agent {
	protected Direction directionQued;
	private boolean open = true;

	public Player(int x, int y)
	{
		super(x, y);
		this.directionQued = null;
	}

	public void tic(Game game, int counter)
	{
		if (validDirection(directionQued)) {
			this.direction = this.directionQued;
		}

		if (direction != null && validDirection(direction)) {
			Point point = translate(direction, 1);
			this.x = point.x;
			this.y = point.y;
		}

		App app = game.app;

		Sprite sprite = getSprite(counter);
		//app.image(game.allSprites.get(sprite), displayX(), displayY());
	}

	public void setQuedDirection(Direction newDirection)
	{
		if (newDirection != null) {
			this.directionQued = newDirection;
		}
	}

	public Sprite getSprite(int counter)
	{
		// toggle whether Waka has mouth open
		if (counter % 8 == 0) {
			open = (open) ? false : true;
		}

		Sprite sprite = null;
		if (open && direction != null) {
			switch (direction) {
				case right:  sprite = Sprite.playerRight;  break;
				case left:   sprite = Sprite.playerLeft;   break;
				case up:     sprite = Sprite.playerUp;     break;
				case down:   sprite = Sprite.playerDown;   break;
			}
		} else {
			sprite = (open) ? Sprite.playerRight : Sprite.playerClosed;
		}

		return sprite;
	}

	public Sprite staticSprite()
	{
		return Sprite.playerRight;
	}
}
