package ghost;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Arrays;

import processing.core.PImage;
import processing.core.PApplet;

public class Ghost extends Agent {
	private static Map<Type, PImage> sprites;
	private static PImage frightened;

	private static List<Integer> modeLengths;
	private static boolean scatter;
	private static int mode;
	private Type type;

	public Ghost(int x, int y, char typeOfGhost)
	{
		super(x, y);
		this.scatter = false;
		this.direction = null;
		this.type = null;

		if (typeOfGhost == 'a') {
			this.type = Type.ambusher;
		} else if (typeOfGhost == 'c') {
			this.type = Type.chaser;
		} else if (typeOfGhost == 'i') {
			this.type = Type.ignorant;
		} else if (typeOfGhost == 'w') {
			this.type = Type.whim;
		}
	}

	public static void toggleScatter()
	{
		Ghost.scatter = (Ghost.scatter) ? false : true;
	}

	public boolean tic(Player player, int counter)
	{
		List<Direction> validMoves = validDirections();

		System.out.println(counter);
		System.out.println(modeLengths.get(mode));
		if (counter > modeLengths.get(mode % modeLengths.size())) {
			System.out.println((int)(counter/60 % modeLengths.size()));
			toggleScatter();
			mode++;
		}

		// decide direction based on ghost type
		validMoves.sort( (a, b) -> {
				Point target = type.target.at(getPoint());
				return Math.abs(translate(a,1).distance(target))
							 - Math.abs(translate(b,1).distance(target));
		});

		direction = validMoves.get(0);
		Point point = translate(direction, 1);

		this.x = point.x;
		this.y = point.y;

		return player.getPoint().distance(this.getPoint()) < 10;
	}

	public void draw(App app, int counter)
	{
		app.image(getSprite(), displayX(), displayY());
		Point target = type.target.at(getPoint());

		if (app.debugMode) {
			app.beginShape();
			app.stroke(256,256,256);
			app.line(x + 9, y + 9, target.x + 9, target.y + 9);
			app.endShape();
		}
	}

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

	public PImage getSprite()
	{
		return (scatter) ? frightened : Ghost.sprites.get(type);
	}

	public Type getType()
	{
		return type;
	}

	public static void setUp(Player player, List<Integer> modeLengths,
			Map<Type, PImage> sprites, PImage frightened)
	{
		Navigate.TARGET = player;
		Ghost.sprites = sprites;
		Ghost.frightened = frightened;
		Ghost.modeLengths = modeLengths;
	}

	@FunctionalInterface
	interface Lambda {
		public Point at(Point current);
	}

	enum Type
	{
		ambusher((point) -> Navigate.ambusher(point)),
		ignorant((point) -> Navigate.ignorant(point)),
		chaser((point) -> Navigate.chaser(point)),
		whim((point) -> Navigate.whim(point));

		Lambda target;

		Type(Lambda target) {
			this.target = target;
		}
	}

	static class Navigate
	{
		static Agent TARGET;

		static Point AMBUSHER = null;
		static Point IGNORANT = null;
		static Point CHASER = null;
		static Point WHIM = null;

		public static Point ambusher(Point current)
		{
			AMBUSHER = current;

			if (Ghost.scatter) {
				return new Point(448, 50);
			} else {
				Point point = TARGET.translate(TARGET.getDirection(), 4 * 16);
				return Utilities.restrictRange(point);
			}
		}

		public static Point chaser(Point current)
		{
			CHASER = current;

			if (Ghost.scatter) {
				return new Point(0,50);
			} else {
				return Utilities.restrictRange(TARGET.getPoint());
			}
		}

		public static Point ignorant(Point current)
		{
			IGNORANT = current;

			int distance = Math.abs(TARGET.getPoint().distance(current));
			if (Ghost.scatter || distance <= 8 * 16) {
				return new Point(0,576);
			} else {
				return Utilities.restrictRange(TARGET.getPoint());
			}
		}

		public static Point whim(Point current)
		{
			WHIM = current;

			if (Ghost.scatter || CHASER == null) {
				return new Point(448, 576);
			} else {
				Point chaser = CHASER;
				Point target = TARGET.translate(TARGET.getDirection(), 2 * 16);

				int X = CHASER.x + 2 * ( target.x - CHASER.x );
				int Y = CHASER.y + 2 * ( target.y - CHASER.y );

				return Utilities.restrictRange(new Point(X, Y));
			}
		}
	}
}
