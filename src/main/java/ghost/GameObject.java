package ghost;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent game objects that the player can interact with and specify their
 * interactions with the game.
 */
public class GameObject extends Coordinate {

	/**
	 * Whether the game object has been eaten.
	 */
	private boolean eaten;

	/**
	 * The type of game object.
	 */
	final Type type;

	/**
	 * Types of game objects.
	 */
	enum Type {
		fruit(Sprite.fruit),
		superFruit(Sprite.superFruit),
		soda(Sprite.soda);

		Sprite sprite;

		Type(Sprite sprite) {
			// store associated sprite
			this.sprite = sprite;
		}
	}

	/**
	 * Initialise a game object with coordinates and a type.
	 * @param x, integer x coordinate
	 * @param y, integer y coordinate
	 * @param sprite, sprite representing type of game object
	 */
	public GameObject(int x, int y, Sprite sprite) {
		super(x, y);

		Type type = null;

		if (sprite != null) {

			switch (sprite) {
				case superFruit:
					type = Type.superFruit;
					break;
				case fruit:
					type = Type.fruit;
					break;
				case soda:
					type = Type.soda;
					break;
			}

		}

		this.type = type;
	}

	// Getter and setter methods

	/**
	 * If the gameObject has a valid type, return the associated sprite.
	 * @return associated sprite with given type
	 */
	public Sprite getSprite() {
		return (type == null) ? null : type.sprite;
	}

	/**
	 * Return whether the object has been eaten.
	 * @return whether the object has been eaten
	 */
	public boolean isEaten() {
		return eaten;
	}

	/**
	 * Resets the eaten state of a game object to false.
	 */
	public void reset() {
		this.eaten = false;
	}

	// collision methods (when player eats specific object)

	/**
	 * Queues frightened mode.
	 * @param game, the game instance
	 */
	public static void superFruit(Game game) {
		game.modeControl.queueMode(Ghost.Mode.FRIGHTENED);
	}

	/**
	 * Adds one to game points.
	 * @param game, the game instance
	 */
	public static void fruit(Game game) {
		game.points++;
	}

	/**
	 * Queues frightened mode.
	 * @param game, the game instance
	 */
	public static void soda(Game game) {
		game.modeControl.queueMode(Ghost.Mode.INVISIBLE);
	}

	// Active methods

	/**
	 * Returns false if the player is close enough and sets the state to eaten,
	 * else returns true if the player was not close enough.
	 * @param player, a player object
	 * @return returns true for no interaction between game object and player
	 */
	public boolean evolve(Player player) {

		if (point().distance(player.point()) < 10) {
			eaten = true;
			return false;
		}

		return true;

	}

	/**
	 * Draw a game object to the screen.
	 * @param game, a game object
	 */
	public void draw(Game game) {
		game.draw(getSprite(), x, y, null);
	}

	/**
	 * Update all instances of gameObjects in a game instance. If evolve returns
	 * false and the object has not yet been eaten, trigger associated method to
	 * update the game instance.
	 * @param game, a game instance
	 */
	public static void TIC(Game game) {

		for (GameObject obj : game.gameObjects) {

			if (!obj.isEaten() && !obj.evolve(game.player)) {

				switch (obj.type) {
					case superFruit:
						superFruit(game);
						break;
					case fruit:
						fruit(game);
						break;
					case soda:
						soda(game);
						break;
				}

			}

		}

	}

}
