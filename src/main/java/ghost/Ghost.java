package ghost;

import java.util.stream.Collectors;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Ghost extends Agent {
	private static Map<String, Point> corners;
	private static Ghost CHASER;
	static Mode MODE;

	/**
	 * Whether the ghost is alive
	 */
	private boolean alive;

	/**
	 * Type of ghost
	 */
	final Type type;

	public static void SETUP(Configuration config) {
		Ghost.corners = config.corners;
		CHASER = null;
	}

	public Ghost(int x, int y, Sprite typeOfGhost) {
		super(x, y);
		this.alive = true;

		if (typeOfGhost == Sprite.ambusher) {
			this.type = Type.ambusher;
		} else if (typeOfGhost == Sprite.chaser) {
			this.type = Type.chaser;
		} else if (typeOfGhost == Sprite.ignorant) {
			this.type = Type.ignorant;
		} else if (typeOfGhost == Sprite.whim) {
			this.type = Type.whim;
		} else {
			this.type = null;
		}
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
	 * Returns current mode.
	 * @return the current mode
	 */
	public static Mode MODE() {
		return MODE;
	}

	/**
	 * Returns current mode.
	 * @return the current mode
	 */
	public static Ghost CHASER() {
		return CHASER;
	}

	/**
	 * Sets mode.
	 */
	public static void setMode(Mode mode) {
		MODE = mode;
	}

	/**
	 * Sets chaser.
	 */
	public static void setChaser(Ghost chaser) {
		CHASER = chaser;
	}

	/**
	 * Resets ghost to initial position and sets alive to true. This
	 * partially overwrites the super method of Agent.
	 k/
	public void reset() {
		super.reset();
		this.alive = true;
	}

	/**
	 * Returns a Sprite based on the type of ghost and if the ghost is
	 * frightened. If the mode is FRIGHTENED return the frightened sprite,
	 * else return the sprite determined by the type of ghost.
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
			sprite = type.sprite;
		}

		return sprite;
	}

	// Game related methods

	public static void TIC(Game game) {
		// Update mode
		for (Ghost ghost : game.GHOSTS) {
			if (ghost.isAlive()) {

				Boolean result = ghost.evolve(game.PLAYER);

				// replace the chaser if collision occurs
				if (result == null) {
					List<Ghost> chasers = game.GHOSTS.stream()
						.filter(g -> g.type == Ghost.Type.chaser && g.isAlive())
						.collect(Collectors.toList());
					
					CHASER = (chasers.size() > 0) ? chasers.get(0) : null;
				} else if (!result) {
					ghost.direction = ghost.nextDirection(game.PLAYER);
					game.reset();
				}
			}

		}

	}

	public Boolean evolve(Player PLAYER) {
		direction = nextDirection(PLAYER);

		// collision detection

		Point player = PLAYER.point();
		if (point().distance(player) < 16 && (x == player.x || y == player.y)) {

			if (MODE == Mode.FRIGHTENED) {
				alive = false;

				// null means get new chaser
				return (type == Type.chaser) ? null : true;
			}

			// false means soft reset the game
			return false;

		} else {
			// Move ghost
			Point point = translate(direction, 1);
			this.x = point.x;
			this.y = point.y;

			return true;
		}
	}

	public boolean draw(Game game) {
		Point target = null;
		isCorner = true;

		if (MODE != Mode.FRIGHTENED && MODE != null) {
			target = target(game.PLAYER);

			int w  = 16 * SPRITE_MAP[0].length; // Width
			int h = 16 * SPRITE_MAP.length;     // Height

			boolean TL  = target.x == 0 && target.y == 0;
			boolean TR  = target.x == w && target.y == 0;
			boolean BL  = target.x == 0 && target.y == h;
			boolean BR  = target.x == w && target.y == h;

			if (!TL && !TR && !BL && !BR) {
				target.x += 7;
				target.y += 7;
				isCorner = false;
			}
		}

		game.draw(getSprite(), displayX(), displayY(), target);
		return isCorner;
	}

	// Navigation

	public List<Direction> validDirections() {
		// unless cornered, a ghost cannot turn backwards
		List<Direction> valid = super.validDirections();

		if (direction != null && valid.size() > 1) {
			return valid.stream()
				.filter(d -> d != direction.getOpposite())
				.collect(Collectors.toList());
		} else {
			return valid;
		}
	}

	public Direction nextDirection(Player PLAYER) {
		List<Direction> valid = validDirections();

		if (valid.size() > 1) {
			Point target = target(PLAYER);
			valid.sort( (a, b) -> {
					return abs(translate(a,1).distance(target))
								 - abs(translate(b,1).distance(target));
			});
		}

		if (MODE == Mode.FRIGHTENED) {
			int random = new Random().nextInt(valid.size());
			return valid.get(random);
		} else  {
			return valid.get(0);
		}
	}

	public Point target(Player PLAYER) {
		if (type == Type.ambusher) {
			return Ghost.ambusher(PLAYER);
		} else if (type == Type.ignorant) {
			return Ghost.ignorant(point(), PLAYER);
		} else if (type == Type.chaser) {
			return Ghost.chaser(PLAYER);
		} else {
			return Ghost.whim(PLAYER);
		}
	}

	public static Point ambusher(Player PLAYER) {
		if (MODE == Mode.SCATTER) {
			int width = 16 * SPRITE_MAP[0].length;
			return new Point(width, 0);
		} else {
			Point point = PLAYER.translate(PLAYER.direction(), 4 * 16);
			return point.restrictRange(16*SPRITE_MAP[0].length, 16*SPRITE_MAP.length);
		}
	}

	public static Point chaser(Player PLAYER) {
		if (MODE == Mode.SCATTER) {
			return new Point(0, 0);
		} else {
			Point point = PLAYER.point();
			return point.restrictRange(16*SPRITE_MAP[0].length, 16*SPRITE_MAP.length);
		}
	}

	public static Point whim(Player PLAYER) {
		if (MODE == Mode.SCATTER) {
			int width  = 16 * SPRITE_MAP[0].length;
			int height = 16 * SPRITE_MAP.length;
			return  new Point(width, height);
		} else if (CHASER == null) {
			// target player
			return PLAYER.point();
		} else {
			Point chaser = CHASER.point();
			Point target = PLAYER.translate(PLAYER.direction(), 2 * 16);

			int X = chaser.x + 2 * ( target.x - chaser.x );
			int Y = chaser.y + 2 * ( target.y - chaser.y );

			Point point = new Point(X, Y);

			return point.restrictRange(16*SPRITE_MAP[0].length, 16*SPRITE_MAP.length);
		}
	}

	public static Point ignorant(Point current, Player PLAYER) {
		int distance = abs(PLAYER.point().distance(current));
		if (MODE == Mode.SCATTER || distance <= 8 * 16) {
			int height = 16 * SPRITE_MAP.length;
			return new Point(0, height);
		} else {
			return PLAYER.point();
		}
	}

	// Types of GHOSTS and modes

	enum Type {
		ambusher(Sprite.ambusher),
		ignorant(Sprite.ignorant),
		chaser(Sprite.chaser),
		whim(Sprite.whim);

		Sprite sprite;

		Type(Sprite sprite) {
			this.sprite = sprite;
		}
	}

	enum Mode {
		INVISIBLE,
		FRIGHTENED,
		CHASE,
		SCATTER;
	}
}
