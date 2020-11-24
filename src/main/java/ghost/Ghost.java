package ghost;

import java.util.stream.Collectors;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class Ghost extends Agent {
	static private Ghost CHASER;
	static private Mode MODE;

	/**
	 * Whether the ghost is alive
	 */
	private boolean alive;

	/**
	 * Type of ghost
	 */
	final Type type;

	public Ghost(int x, int y, Sprite typeOfGhost) {
		super(x, y);
		this.alive = true;

		if (typeOfGhost == Sprite.ghostAmbusher) {
			this.type = Type.ambusher;
		} else if (typeOfGhost == Sprite.ghostChaser) {
			this.type = Type.chaser;
		} else if (typeOfGhost == Sprite.ghostIgnorant) {
			this.type = Type.ignorant;
		} else if (typeOfGhost == Sprite.ghostWhim) {
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
	 * Resets ghost to initial position and sets alive to true. This
	 * partially overwrites the super method of Agent.
	 */
	public void softReset() {
		super.softReset();
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

		if (MODE != Mode.FRIGHTENED) {
			switch (type) {
				case ambusher:
					sprite = Sprite.ghostAmbusher;
					break;
				case ignorant:
					sprite = Sprite.ghostIgnorant;
					break;
				case chaser:
					sprite = Sprite.ghostChaser;
					break;
				case whim:
					sprite = Sprite.ghostWhim;
					break;
			}
		} else {
			sprite = Sprite.ghostFrightened;
		}

		return sprite;
	}

	public static void setMode(Mode mode) {
		MODE = (mode != null) ? mode : MODE;
	}

	public static Mode getMode() {
		return MODE;
	}

	// Game related methods

	public boolean tic(List<Ghost> GHOSTS, Player PLAYER) {
		direction = nextDirection(PLAYER);

		// collision detection

		Point player = PLAYER.point();
		if ((x == player.x || y == player.y) && point().distance(player) < 15) {
			if (type == Type.chaser) {
				List<Ghost> chasers = GHOSTS.stream()
					.filter(g -> g.alive && g.type == Type.chaser)
					.collect(Collectors.toList());

				CHASER = (chasers.size() > 0) ? chasers.get(0) : null;
			}

			if (MODE == Mode.FRIGHTENED) {
				alive = false;
			} else {
				return false;
			}
		} else {
			// Move ghost
			Point point = translate(direction, 1);
			this.x = point.x;
			this.y = point.y;
		}

		return true;
	}

	public void draw(Game game) {
		Point target = null;

		if (MODE != Mode.FRIGHTENED && MODE != null) {
			target = target(game.PLAYER);
			target.x += (MODE == Mode.SCATTER) ? 0 : 8;
			target.y += (MODE == Mode.SCATTER) ? 0 : 8;
		}

		game.draw(getSprite(), displayX(), displayY(), target);
	}

	// Navigation

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
			return Agent.TOP_RIGHT;
		} else {
			Point point = PLAYER.translate(PLAYER.direction(), 4 * 16);
			return point.restrictRange(16*SPRITE_MAP[0].length, 16*SPRITE_MAP.length);
		}
	}

	public static Point chaser(Player PLAYER) {
		if (MODE == Mode.SCATTER) {
			return Agent.TOP_LEFT;
		} else {
			Point point = PLAYER.point();
			return point.restrictRange(16*SPRITE_MAP[0].length, 16*SPRITE_MAP.length);
		}
	}

	public static Point whim(Player PLAYER) {
		if (MODE == Mode.SCATTER) {
			return Agent.BOT_RIGHT;
		} else if (CHASER == null) {
			// target player
			return chaser(PLAYER);
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
			return Agent.BOT_LEFT;
		} else {
			return chaser(PLAYER);
		}
	}

	// Types of GHOSTS and modes

	enum Type {
		ambusher,
		ignorant,
		chaser,
		whim;
	}

	enum Mode {
		FRIGHTENED,
		CHASE,
		SCATTER;
	}
}
