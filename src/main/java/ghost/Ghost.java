package ghost;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.Comparator;
import java.util.Arrays;
import java.util.stream.Collectors;

import processing.core.PImage;
import processing.core.PApplet;

public class Ghost extends Agent {
	private static Queue<Integer> modeQueue;
	private static boolean scatter;
	final Type type;

	public Ghost(int x, int y, Sprite typeOfGhost)
	{
		super(x, y);
		this.scatter = false;
		this.direction = null;

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

	public static boolean isScatter()
	{
		return scatter;
	}

	public static void toggleScatter()
	{
		Ghost.scatter = (Ghost.scatter) ? false : true;
	}

	public Sprite getSprite()
	{
		Sprite sprite = null;

		if (!scatter) {
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

	// Game related methods

	public static void setup(Player player, List<Integer> modeLengths)
	{
		Type.PLAYER = player;
		Ghost.scatter = false;


		// Build modeLengths queue
		Ghost.modeQueue = new LinkedList<>();

		for (int j=0; j < modeLengths.size(); j++) {
			for (int i=0; i < j; i++) {
				Ghost.modeQueue.add(modeLengths.get(j) + modeLengths.get(i));
			}
		}

		List<Point> pointList = new ArrayList<>();
		for (int j=0; j < spriteMap.length; j++) {
			for (int i=0; i < spriteMap[0].length; i++) {
				if (spriteMap[j][i] != null && !spriteMap[j][i].isWall()) {
					pointList.add(new Point(16 * i, 16 * j));
				}
			}
		}

		// Corners of map
		Point bottomRight = new Point(16 * spriteMap[0].length, 16 * spriteMap.length);
		Point bottomLeft = new Point(0 , 16 * spriteMap.length);
		Point topRight = new Point(16 * spriteMap[0].length, 0);
		Point topLeft = new Point(0 , 0);

		// Get corners
		pointList.sort((a, b) -> a.distance(bottomRight) - b.distance(bottomRight));
		Type.BOT_RIGHT = pointList.get(0);

		pointList.sort((a, b) -> a.distance(bottomLeft) - b.distance(bottomLeft));
		Type.BOT_LEFT = pointList.get(0);

		pointList.sort((a, b) -> a.distance(topRight) - b.distance(topRight));
		Type.TOP_RIGHT = pointList.get(0);

		pointList.sort((a, b) -> a.distance(topLeft) - b.distance(topLeft));
		Type.TOP_LEFT = pointList.get(0);
	}

	public boolean tic(Player player, int counter)
	{
		refreshMode(counter);
		List<Direction> validMoves = validDirections();

		/*
 		System.out.println(modeLengths);
 		System.out.println(modeLengths.get(mode[0]));
		System.out.println();
		*/

		// decide direction based on ghost type
		validMoves.sort( (a, b) -> {
				Point target = type.target(getPoint());
				return Math.abs(translate(a,1).distance(target))
							 - Math.abs(translate(b,1).distance(target));
		});

		direction = validMoves.get(0);
		Point point = translate(direction, 1);

		this.x = point.x;
		this.y = point.y;

		return player.getPoint().distance(this.getPoint()) < 10;
	}

	public void draw(Game game, int counter)
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

	public boolean refreshMode(int counter)
	{
		System.out.println(counter + ", breakpoint: " + 60 * modeQueue.peek());
		System.out.println();
		if (modeQueue.peek() == null) {
			return false;
		} else {
			if (counter > 60 * modeQueue.peek()) {
				modeQueue.remove();
				toggleScatter();
			}

			return true;
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

	public static Point ambusher(Point current, Player player)
	{
		if (Ghost.scatter) {
			return Type.TOP_RIGHT;
		} else {
			Point point = player.translate(player.getDirection(), 4 * 16);
			return point.restrictRange(448, 576);
		}
	}

	public static Point chaser(Point current, Player player)
	{
		if (Ghost.scatter) {
			return Type.TOP_LEFT;
		} else {
			return player.getPoint().restrictRange(448, 576);
		}
	}

	public static Point ignorant(Point current, Player player)
	{
		int distance = Math.abs(player.getPoint().distance(current));
		if (Ghost.scatter || distance <= 8 * 16) {
			return Type.BOT_LEFT;
		} else {
			return player.getPoint().restrictRange(448, 576);
		}
	}

	public static Point whim(Point current, Player player)
	{
		if (Ghost.scatter || Type.CHASER() == null) {
			return Type.BOT_RIGHT;
		} else {
			Point chaser = Type.CHASER();
			Point target = player.translate(player.getDirection(), 2 * 16);

			int X = chaser.x + 2 * ( target.x - chaser.x );
			int Y = chaser.y + 2 * ( target.y - chaser.y );

			return (new Point(X, Y)).restrictRange(448, 576);
		}
	}

	// Types of ghosts

	enum Type
	{
		ambusher,
		ignorant,
		chaser,
		whim;

		static List<Ghost> CHASERS = new ArrayList<>();
		static Player PLAYER = null;

		static Point TOP_RIGHT = null;
		static Point BOT_RIGHT = null;
		static Point TOP_LEFT = null;
		static Point BOT_LEFT = null;

		public static Point CHASER() {
			if (CHASERS.size() > 0) {
				if (CHASERS.get(0).alive) {
					return CHASERS.get(0).getPoint();
				} else {
					CHASERS.remove(0);
					return CHASER();
				}
			} else {
				return null;
			}
		}

		public Point target(Point current)
		{
			if (this == Type.ambusher) {
				return Ghost.ambusher(current, PLAYER);
			} else if (this == Type.ignorant) {
				return Ghost.ignorant(current, PLAYER);
			} else if (this == Type.chaser) {
				return Ghost.chaser(current, PLAYER);
			} else {
				return Ghost.whim(current, PLAYER);
			}
		}
	}
}
