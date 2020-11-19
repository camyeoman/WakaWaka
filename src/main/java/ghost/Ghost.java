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

public class Ghost extends Agent {
	static private Mode mode;
	static Player PLAYER;
	final Type type;

	public Ghost(int x, int y, Sprite typeOfGhost)
	{
		super(x, y);

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

	public static void setup(Configuration config, Player player, List<Ghost> ghosts)
	{
		PLAYER = player;

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

		// get a list of chasers

		Type.CHASERS = ghosts.stream()
			.filter(x -> x.type == Type.chaser)
			.collect(Collectors.toList());
	}

	// Getter and Setter methods

	public Sprite getSprite()
	{
		Sprite sprite = null;

		if (mode != Mode.FRIGHTENED) {
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
		return mode;
	}

	// Game related methods

	public boolean tic(Game game)
	{
		int frames = game.frames;
		PLAYER = game.player;

		// player collision detection

		Point playerPoint, ghostPoint;
		playerPoint = PLAYER.getPoint().gridSnap(PLAYER.getDirection());
		ghostPoint = getPoint().gridSnap(direction);

		if (ghostPoint.distance(playerPoint) == 0) {
			if (mode == Mode.FRIGHTENED) {
				game.ghosts.remove(this);
				if (type == Type.chaser) {
					Type.CHASERS.remove(this);
				}
			} else {
				game.lives += -1;
				if (game.lives > 0) {
					game.ghosts.stream().forEach(Agent::softReset);
					game.player.softReset();
				} else {
					return false;
				}
			}
		}

		// Get current mode

		Mode mode = game.modeControl.mode();

		// decide direction based on ghost type and mode

		List<Direction> validMoves = validDirections();

		validMoves.sort( (a, b) -> {
				Point target = type.target(getPoint());
				return Math.abs(translate(a,1).distance(target))
							 - Math.abs(translate(b,1).distance(target));
		});

		direction = validMoves.get(0);

		// Move ghost in specified direction
		
		Point point = translate(direction, 1);
		this.x = point.x;
		this.y = point.y;

		return true;
	}

	public void draw(Game game, int frames)
	{
		App app = game.app;

		app.image(game.allSprites.get(getSprite()), displayX(), displayY());
		Point target = type.target(getPoint());

		if (app.debugMode) {
			app.beginShape();
			app.stroke(256,256,256);
			app.line(x + 9, y + 9, target.x + 9, target.y + 9);
			app.endShape();
		}
	}

	// Navigation

	public List<Direction> validDirections()
	{
		// unless cornered, a ghost cannot turn backwards
		List<Direction> valid = super.validDirections();
		if (valid.size() > 1) {
			if (direction != null && valid.contains(direction.getOpposite())) {
				valid.remove(direction.getOpposite());
			}
		}

		return valid;
	}

	public static Point ambusher(Point current)
	{
		if (mode == Mode.FRIGHTENED) {
			return Type.TOP_RIGHT;
		} else {
			Point point = PLAYER.translate(PLAYER.getDirection(), 4 * 16);
			return point.restrictRange(16*spriteMap[0].length, 16*spriteMap.length);
		}
	}

	public static Point chaser(Point current)
	{
		if (mode == Mode.FRIGHTENED) {
			return Type.TOP_LEFT;
		} else {
			Point point = PLAYER.getPoint();
			return point.restrictRange(16*spriteMap[0].length, 16*spriteMap.length);
		}
	}

	public static Point ignorant(Point current)
	{
		int distance = Math.abs(PLAYER.getPoint().distance(current));
		if (mode == Mode.FRIGHTENED || distance <= 8 * 16) {
			return Type.BOT_LEFT;
		} else {
			return chaser(current);
		}
	}

	public static Point whim(Point current)
	{
		if (mode == Mode.FRIGHTENED) {
			return Type.BOT_RIGHT;
		} else if (Type.CHASER() == null) {
			// target player
			return chaser(current);
		} else {
			Point chaser = Type.CHASER();
			Point target = PLAYER.translate(PLAYER.getDirection(), 2 * 16);

			int X = chaser.x + 2 * ( target.x - chaser.x );
			int Y = chaser.y + 2 * ( target.y - chaser.y );

			Point point = new Point(X, Y);
			return point.restrictRange(16*spriteMap[0].length, 16*spriteMap.length);
		}
	}

	// Types of ghosts and modes

	enum Type
	{
		ambusher,
		ignorant,
		chaser,
		whim;

		static List<Ghost> CHASERS;

		static Point TOP_RIGHT = null;
		static Point BOT_RIGHT = null;
		static Point TOP_LEFT = null;
		static Point BOT_LEFT = null;

		public static Point CHASER() {
			if (CHASERS.size() > 0) {
				return CHASERS.get(0).getPoint();
			}

			return null;
		}

		public Point target(Point current)
		{
			if (mode == Mode.FRIGHTENED) {
				// return random point as target

				int x = (int) Math.random() * (spriteMap[0].length + 1);
				int y = (int) Math.random() * (spriteMap.length + 1);
				
				return new Point(x, y);
			}

			if (this == Type.ambusher) {
				return Ghost.ambusher(current);
			} else if (this == Type.ignorant) {
				return Ghost.ignorant(current);
			} else if (this == Type.chaser) {
				return Ghost.chaser(current);
			} else {
				return Ghost.whim(current);
			}
		}
	}

	enum Mode
	{
		FRIGHTENED,
		CHASE,
		SCATTER;
	}
}

