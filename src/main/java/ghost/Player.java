package ghost;

import processing.core.PApplet;

public class Player extends Agent {
	private Direction queuedDirection;
	private boolean open;

	/**
	* Initialises a player with an x and a y coordinate.
	* @param x, the x coordinate
	* @param y, the y coordinate
	*/
	public Player(int x, int y) {
		super(x, y);
		this.queuedDirection = null;
		this.open = true;
	}

	// Getter and Setter methods

	/**
	 * Resets player to initial position and resets all directions. This
	 * partially overwrites the super method of Agent to properlly reset
	 * the player object by including the queuedDirection attribute.
	 */
	public void softReset() {
		super.softReset();
		this.queuedDirection = null;
	}

	public void setQuedDirection(Direction newDirection) {
		if (newDirection != null) {
			this.queuedDirection = newDirection;
		}
	}

	/**
	 * Returns a Sprite based on the direction and frame count. Every 8
	 * frames the sprite is toggled to open/closed, and while open the
	 * sprite depends on the direction of travel. If the direction is
	 * null then then the sprite facing right is shown.
	 * @param frames, the number of frames elapsed since the game began
	 * @return a Sprite representing current state
	 */
	public Sprite getSprite(int frames) {
		// toggle whether Waka has mouth open
		if (frames % 8 == 0) {
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

	// Active methods

	/**
	 * Updates the player direction and position. If the queued direction
	 * is valid, it is set as the current direction. If the direction is
	 * valid the player is moved in said direction.
	 */
	public void tic() {
		if (validDirection(queuedDirection)) {
			this.direction = this.queuedDirection;
		}

		if (direction != null && validDirection(direction)) {
			Point point = translate(direction, 1);
			this.x = point.x;
			this.y = point.y;
		}
	}

	/**
	 * Sets the queuedDirection attribute. This is used to interface with
	 * user input to move the player, as the user input is stored for
	 * the next time the player is able to change direction.
	 * @param newDirection, the new direction to que
	 */
	public boolean readInput(int keyCode)
	{
		int current = (direction == null) ? 0 : direction.KEY_CODE;
		if (direction != null || keyCode != current) {
			switch (keyCode) {
				case 37:
					queuedDirection = Direction.left;
					break;
				case 38:
					queuedDirection = Direction.up;
					break;
				case 39:
					queuedDirection = Direction.right;
					break;
				case 40:
					queuedDirection = Direction.down;
					break;
			}

			return true;
		}

		return false;
	}

	/**
	 * Draws the player.
	 * @param frames, the number of frames elapsed since the game began
	 * @param game, the current game object
	 */
	public void draw(Game game) {
		int frames = game.frames;
		App app = game.app;
		

		Sprite sprite = getSprite(frames);
		app.image(game.allSprites.get(sprite), displayX(), displayY());
	}
}
