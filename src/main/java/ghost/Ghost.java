package ghost;

import java.util.stream.Collectors;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Represents the ghost character.
 */
public class Ghost extends Agent {

	/**
	 * Store a ghost of type chaser.
	 */
	private static Ghost CHASER;

	/**
	 * Whether the ghost is alive
	 */
	private boolean alive;

	/**
	 * Current mode of the ghosts.
	 */
	private static Mode MODE;

	/**
	 * Different ghost modes.
	 */
	enum Mode {
		INVISIBLE,
		FRIGHTENED,
		CHASE,
		SCATTER;
	}

	/**
	 * Type of ghost
	 */
	final Type type;

	/**
	 * Different ghost types. The corresponding Sprite object for each type is
	 * stored under the sprite attrubute.
	 */
	enum Type {
		ambusher(Sprite.ambusher),
		ignorant(Sprite.ignorant),
		chaser(Sprite.chaser),
		whim(Sprite.whim);

		Sprite sprite;

		Type(Sprite sprite) {
			// store associated sprite.
			this.sprite = sprite;
		}
	}

	/**
	 * Initialise a ghost object with an x, y coordinate, a type, and as alive.
	 * Also extends the constructor from the super class Agent.
	 * @param x, integer x coordinate
	 * @param y, integer y coordinate
	 * @param typeOfGhost, Sprite object associated with type of ghost
	 */
	public Ghost(int x, int y, Sprite typeOfGhost) {

		super(x, y);
		this.alive = true;

		Type type = null;

		if (typeOfGhost != null) {

			switch (typeOfGhost) {
				case ambusher:
					type = Type.ambusher;
					break;
				case chaser:
					type = Type.chaser;
					break;
				case ignorant:
					type = Type.ignorant;
					break;
				case whim:
					type = Type.whim;
					break;
			}

		}

		this.type = type;

	}

	// Getter and Setter methods

	/**
	 * Returns if the ghost is alive.
	 * @return if the ghost is alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Sets chaser.
	 * @param chaser, chaser ghost object to set
	 */
	public static void setChaser(Ghost chaser) {
		CHASER = chaser;
	}

	/**
	 * Sets mode.
	 * @param mode, mode to set
	 */
	public static void setMode(Mode mode) {
		MODE = mode;
	}

	/**
	 * Resets ghost to initial position and sets alive to true. This partially
	 * overwrites the super method of Agent.
	 */
	public void reset() {
		super.reset();
		this.alive = true;
	}

	/**
	 * Returns a Sprite based on the type of ghost and the current mode. If the
	 * mode is FRIGHTENED return the frightened sprite, if the mode is INVISIBLE
	 * return the invisible sprite. Else return the sprite assocaited with the
	 * type of ghost.
	 * @return a Sprite representing current state
	 */
	public Sprite getSprite() {

		Sprite sprite = null;

		if (type == null) {
			return sprite;
		}

		if (MODE == Mode.INVISIBLE) {
			sprite = Sprite.invisible;
		} else if (MODE == Mode.FRIGHTENED) {
			sprite = Sprite.frightened;
		} else {
			// return the sprite associated with the enum Type entry
			sprite = type.sprite;
		}

		return sprite;

	}

	// Game related methods

	/**
	 * Update the state of all ghosts in a game instance. Maintain a current
	 * CHASER (a ghost of type chaser), if one is alive, handle interactions
	 * if evolve returns false (if collision).
	 * @param game, a game object
	 */
	public static void TIC(Game game) {

		for (Ghost ghost : game.ghosts) {
			if (ghost.isAlive()) {

				Boolean result = ghost.evolve(game.player);

				// replace the chaser if collision occurs
				if (result == null) {
					List<Ghost> chasers = game.ghosts.stream()
						.filter(g -> g.type == Ghost.Type.chaser && g.isAlive())
						.collect(Collectors.toList());
					
					CHASER = (chasers.size() > 0) ? chasers.get(0) : null;
				} else if (!result) {
					game.reset();
				}
			}

		}

	}

	/**
	 * Return false if there was a collision between player and ghost, and true
	 * if not. If there was a collision, if the mode is frightened set the
	 * ghost's alive state to false. If evolve returns null replace the current
	 * CHASER, and if no collision occured update the position of the ghost.
	 * @param player, a player object
	 * @return false if collision, true if not
	 */
	public Boolean evolve(Player player) {

		direction = nextDirection(player);

		// collision detection

		Point point = player.point();
		if (point().distance(point) < 16 && (x == point.x || y == point.y)) {

			if (MODE == Mode.FRIGHTENED) {
				alive = false;

				// null means get new chaser
				return (type == Type.chaser) ? null : true;
			}

			// false means soft reset the game
			return false;

		} else {
			// Move ghost
			Point translated = translate(direction, 1);
			this.x = translated.x;
			this.y = translated.y;

			return true;
		}

	}

	/**
	 * Returns whether or not a given point is a corner of the map.
	 * @param point, the point in question
	 * @return whether the point is a corner of the map
	 */
	public static boolean isCorner(Point point) {

		int w  = 16 * SPRITE_MAP[0].length; // w - width
		int h = 16 * SPRITE_MAP.length;     // h - height

		boolean TL = point.x == 0 && point.y == 0;
		boolean TR = point.x == w && point.y == 0;
		boolean BL = point.x == 0 && point.y == h;
		boolean BR = point.x == w && point.y == h;

		return TL || TR || BL || BR;

	}

	/**
	 * Draws the ghost object to the screen. If mode is FRIGHTENED
	 * do not draw debug lines, and if the point is not a corner
	 * center the debug lines on the center of the target square.
	 * @param game, the game instance to draw to
	 */
	public void draw(Game game) {

		Point target = null;

		if (MODE != Mode.FRIGHTENED && MODE != null) {
			target = target(game.player);

			if (!isCorner(target)) {
				target.x += 7;
				target.y += 7;
			}

		}

		game.draw(getSprite(), displayX(), displayY(), target);

	}

	// Navigation

	/**
	 * Return all valid direction, accounting for the fact that a ghost cannot
	 * turn backwards unless it has no other choice. If not cornered remove
	 * the opposite direction to the current direction from valid directions.
	 * @return list of valid directions for a ghost
	 */
	public List<Direction> validDirections() {

		List<Direction> valid = super.validDirections();

		// unless cornered, a ghost cannot turn backwards
		if (direction != null && valid.size() > 1) {
			return valid.stream()
				.filter(d -> d != direction.getOpposite())
				.collect(Collectors.toList());
		} else {
			return valid;
		}

	}

	/**
	 * Returns the next direction based on the type of ghost and the current
	 * mode. The type of ghost determines the navigation behaviour, but this
	 * behaviour can be overidden if the mode is FRIGHTENED. To determine
	 * the next direction sort the valid direction by their straight line
	 * distance from the target point.
	 * @param player, a player object
	 * @return the next valid direction
	 */
	public Direction nextDirection(Player player) {
		List<Direction> valid = validDirections();

		if (valid.size() > 1) {
			Point target = target(player);
			valid.sort( (a, b) -> {
					return abs(translate(a,1).distance(target))
								 - abs(translate(b,1).distance(target));
			});
		}

		if (MODE == Mode.FRIGHTENED) {
			int random = new Random().nextInt(valid.size());
			return valid.get(random);
		} else  {
			return (valid.size() > 0) ? valid.get(0) : null;
		}
	}

	/**
	 * Return a point object determined by the type of ghost.
	 * @param player, a player object
	 * @return target point of based on type of ghost
	 */
	public Point target(Player player) {
		if (type == Type.ambusher) {
			return Ghost.ambusher(player);
		} else if (type == Type.ignorant) {
			return Ghost.ignorant(point(), player);
		} else if (type == Type.chaser) {
			return Ghost.chaser(player);
		} else {
			return Ghost.whim(player);
		}
	}

	/**
	 * The target of a amusher type of ghost. This target varies
	 * with the mode and the player position.
	 * @param player, a player object
	 * @return the target point
	 */
	public static Point ambusher(Player player) {

		if (MODE == Mode.SCATTER) {
			int width = 16 * SPRITE_MAP[0].length;
			return new Point(width, 0);
		} else {
			Point point = player.translate(player.direction(), 4 * 16);
			return point.restrictRange(16*SPRITE_MAP[0].length, 16*SPRITE_MAP.length);
		}

	}

	/**
	 * The target of a ignorant type of ghost. This target varies
	 * with the mode and the player position.
	 * @param player, a player object
	 * @return the target point
	 */
	public static Point ignorant(Point current, Player player) {

		int distance = abs(player.point().distance(current));
		if (MODE == Mode.SCATTER || distance <= 8 * 16) {
			int height = 16 * SPRITE_MAP.length;
			return new Point(0, height);
		} else {
			return player.point();
		}

	}

	/**
	 * The target of a chaser type of ghost. This target varies
	 * with the mode and the player position.
	 * @param player, a player object
	 * @return the target point
	 */
	public static Point chaser(Player player) {

		if (MODE == Mode.SCATTER) {
			return new Point(0, 0);
		} else {
			Point point = player.point();
			return point.restrictRange(16*SPRITE_MAP[0].length, 16*SPRITE_MAP.length);
		}

	}

	/**
	 * The target of a whim type of ghost. This target varies
	 * with the mode and the player position.
	 * @param player, a player object
	 * @return the target point
	 */
	public static Point whim(Player player) {

		if (MODE == Mode.SCATTER) {
			int width  = 16 * SPRITE_MAP[0].length;
			int height = 16 * SPRITE_MAP.length;
			return  new Point(width, height);
		} else if (CHASER == null) {
			// target player
			return player.point();
		} else {
			Point chaser = CHASER.point();
			Point target = player.translate(player.direction(), 2 * 16);

			int X = chaser.x + 2 * ( target.x - chaser.x );
			int Y = chaser.y + 2 * ( target.y - chaser.y );

			Point point = new Point(X, Y);

			return point.restrictRange(16*SPRITE_MAP[0].length, 16*SPRITE_MAP.length);
		}

	}

}
