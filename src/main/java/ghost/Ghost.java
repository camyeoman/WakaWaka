package ghost;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Arrays;
import java.util.stream.Collectors;

import processing.core.PImage;
import processing.core.PApplet;

import static java.lang.Math.abs;

public class Ghost extends Agent {
	static private Ghost CHASER;
	static private Mode MODE;

	protected boolean alive;
	final Type type;

	public Ghost(int x, int y, Sprite typeOfGhost)
	{
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

	public void softReset() {
		super.softReset();
		this.alive = true;
	}

	public static void RESET()
	{
		Ghost.MODE = Mode.SCATTER;
	}

	public static void SETUP(Configuration config, Player player)
	{
		List<Point> pointList = new ArrayList<>();
		for (int j=0; j < spriteMap.length; j++) {
			for (int i=0; i < spriteMap[0].length; i++) {
				if (spriteMap[j][i] != null && !spriteMap[j][i].isWall()) {
					pointList.add(new Point(16 * i, 16 * j));
				}
			}
		}

		// Get corners
		Point bottomRight = new Point(16 * spriteMap[0].length, 16 * spriteMap.length);
		pointList.sort((a, b) -> a.distance(bottomRight) - b.distance(bottomRight));
		Type.BOT_RIGHT = pointList.get(0);

		Point bottomLeft = new Point(0 , 16 * spriteMap.length);
		pointList.sort((a, b) -> a.distance(bottomLeft) - b.distance(bottomLeft));
		Type.BOT_LEFT = pointList.get(0);

		Point topRight = new Point(16 * spriteMap[0].length, 0);
		pointList.sort((a, b) -> a.distance(topRight) - b.distance(topRight));
		Type.TOP_RIGHT = pointList.get(0);

		Point topLeft = new Point(0 , 0);
		pointList.sort((a, b) -> a.distance(topLeft) - b.distance(topLeft));
		Type.TOP_LEFT = pointList.get(0);
	}

	public static void UPDATE_MODE(Game game)
	{
		MODE = game.modeControl.update();
	}

	// Getter and Setter methods

	public Sprite getSprite()
	{
		Sprite sprite = null;

		if (MODE != Mode.FRIGHTENED) {
			switch (type) {
				case ambusher:  sprite = Sprite.ghostAmbusher;  break;
				case ignorant:  sprite = Sprite.ghostIgnorant;  break;
				case chaser:    sprite = Sprite.ghostChaser;    break;
				case whim:      sprite = Sprite.ghostWhim;      break;
			}
		} else {
			sprite = Sprite.ghostFrightened;
		}

		return sprite;
	}

	public Mode getMode()
	{
		return MODE;
	}

	// Game related methods

	public boolean tic(Game game)
	{
		int frames = game.frames;

		List<Ghost> ghosts = game.GHOSTS.stream()
			.filter(g -> g.alive && g.type == Type.chaser)
			.collect(Collectors.toList());

		CHASER = (ghosts.size() > 0) ? ghosts.get(0) : null;

		// decide direction based on ghost type and mode

		List<Direction> validMoves = validDirections();
		Point target = target(game.PLAYER);

		validMoves.sort( (a, b) -> {
				return abs(translate(a,1).distance(target))
							 - abs(translate(b,1).distance(target));
		});

		direction = validMoves.get(0);

		if (collide(game.PLAYER)) {
			// player collision
			return false;
		} else {
			// Move ghost in specified direction
			Point point = translate(direction, 1);
			this.x = point.x;
			this.y = point.y;

			return true;
		}
	}

	public void draw(Game game, int frames)
	{
		App app = game.app;

		app.image(game.allSprites.get(getSprite()), displayX(), displayY());
		Point target = target(game.PLAYER);

		if (game.debugMode) {
			app.beginShape();
			app.stroke(256,256,256);
			app.line(x + 9, y + 9, target.x + 9, target.y + 9);
			app.endShape();
		}
	}

	public boolean collide(Player PLAYER)
	{
		Point player = PLAYER.point();

		if ((x == player.x || y == player.y) && point().distance(player) < 10) {
			if (MODE == Mode.FRIGHTENED) {
				alive = false;
			}
			return true;
		}

		return false;
	}

	// Navigation

	public List<Direction> validDirections()
	{
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

	public Point target(Player PLAYER)
	{
		if (MODE == Mode.FRIGHTENED) {

			// return random point as target
			int x = (int) Math.random() * (spriteMap[0].length + 1);
			int y = (int) Math.random() * (spriteMap.length + 1);
			return new Point(x, y);

		} else {

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
	}

	public static Point ambusher(Player PLAYER)
	{
		if (MODE == Mode.FRIGHTENED) {
			return Type.TOP_RIGHT;
		} else {
			Point point = PLAYER.translate(PLAYER.getDirection(), 4 * 16);
			return point.restrictRange(16*spriteMap[0].length, 16*spriteMap.length);
		}
	}

	public static Point chaser(Player PLAYER)
	{
		if (MODE == Mode.FRIGHTENED) {
			return Type.TOP_LEFT;
		} else {
			Point point = PLAYER.point();
			return point.restrictRange(16*spriteMap[0].length, 16*spriteMap.length);
		}
	}

	public static Point whim(Player PLAYER)
	{
		if (MODE == Mode.FRIGHTENED) {
			return Type.BOT_RIGHT;
		} else if (CHASER == null) {
			// target player
			return chaser(PLAYER);
		} else {
			Point chaser = CHASER.point();
			Point target = PLAYER.translate(PLAYER.getDirection(), 2 * 16);

			int X = chaser.x + 2 * ( target.x - chaser.x );
			int Y = chaser.y + 2 * ( target.y - chaser.y );

			Point point = new Point(X, Y);
			return point.restrictRange(16*spriteMap[0].length, 16*spriteMap.length);
		}
	}

	public static Point ignorant(Point current, Player PLAYER)
	{
		int distance = abs(PLAYER.point().distance(current));
		if (MODE == Mode.FRIGHTENED || distance <= 8 * 16) {
			return Type.BOT_LEFT;
		} else {
			return chaser(PLAYER);
		}
	}

	// Types of GHOSTS and modes

	enum Type
	{
		ambusher,
		ignorant,
		chaser,
		whim;

		static Point TOP_RIGHT = null;
		static Point BOT_RIGHT = null;
		static Point TOP_LEFT = null;
		static Point BOT_LEFT = null;
	}

	enum Mode
	{
		FRIGHTENED,
		CHASE,
		SCATTER;
	}
}
