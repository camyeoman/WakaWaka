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
	private static Map<Type, PImage> sprites;
	private static PImage frightened;

	private static List<Integer> modeLengths;
	private static boolean scatter;
	private static int[] mode;
	final Type type;

	public Ghost(int x, int y, Sprite typeOfGhost)
	{
		super(x, y);
		this.scatter = false;
		this.direction = null;
		this.mode = new int[]{ 0, 0 };

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

		if (scatter) {
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
			return new Point(448, 50);
		} else {
			Point point = player.translate(player.getDirection(), 4 * 16);
			return point.restrictRange(448, 576);
		}
	}

	public static Point chaser(Point current, Player player)
	{
		if (Ghost.scatter) {
			return new Point(0,50);
		} else {
			return player.getPoint().restrictRange(448, 576);
		}
	}

	public static Point ignorant(Point current, Player player)
	{
		int distance = Math.abs(player.getPoint().distance(current));
		if (Ghost.scatter || distance <= 8 * 16) {
			return new Point(0,576);
		} else {
			return player.getPoint().restrictRange(448, 576);
		}
	}

	public static Point whim(Point current, Player player)
	{
		if (Ghost.scatter || Type.CHASER() == null) {
			return new Point(448, 576);
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

	// Game related methods

	public static void setup(Player player, List<Integer> modeLengths)
	{
		Type.PLAYER = player;
		Ghost.sprites = sprites;
		Ghost.frightened = frightened;
		Ghost.modeLengths = modeLengths;
	}

	public boolean tic(Player player, int counter)
	{
		List<Direction> validMoves = validDirections();

 		System.out.println(modeLengths);
 		System.out.println(modeLengths.get(mode[0]));
		System.out.println();

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
}
