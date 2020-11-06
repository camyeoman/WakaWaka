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

@FunctionalInterface
interface Target {
	public Point at(Player player, Point current);
}

public class Ghost extends Agent {
	static Map<Type, PImage> sprites;
	static PImage clearSprite;
	static int[] modeLengths;
	public static boolean scatter;
	private Type type;

	enum Type {
		ambusher(new Target() {
			public Point at(Player player, Point unused) {
				if (scatter) {
					return new Point(300, 100);
				} else {
					return player.translate(player.getDirection(), 4 * 16);
				}
			}
		}),

		chaser(new Target() {
			public Point at(Player player, Point unused) {
				if (scatter) {
					return new Point(0,0);
				} else {
					return player.getPoint();
				}
			}
		}),

		ignorant(new Target() {
			public Point at(Player player, Point current) {
				int distance = player.getPoint().distance(current);
				if (scatter || distance <= 8) {
					return new Point(0,576);
				} else {
					return player.getPoint();
				}
			}
		}),

		whim(new Target() {
			public Point at(Player player, Point unused) {
				if (scatter) {
					return new Point(448, 576);
				} else {
					Point chaser = Type.chaser.target.at(player,null);
					Point target = player.translate(player.getDirection(), 2 * 16);

					int finalX = 2 * ( target.x - chaser.x );
					int finalY = 2 * ( target.y - chaser.y );

					return new Point(finalX, finalY);
				}
			}
		});

		Target target;

		Type(Target target) {
			this.target = target;
		}
	}

	public Ghost(int x, int y, char typeOfGhost)
	{
		super(x, y);
		this.direction = null;
		switch (typeOfGhost) {
			case 'a':
				this.type = Type.ambusher;
				break;
			case 'c':
				this.type = Type.chaser;
				break;
			case 'i':
				this.type = Type.ignorant;
				break;
			case 'w':
				this.type = Type.whim;
				break;
		}
	}

	public static void loadSprites(App app)
	{
		sprites = new HashMap<>();
		sprites.put(Type.ambusher, Utilities.pathLoad(app, "ambusher"));
		sprites.put(Type.chaser, Utilities.pathLoad(app, "chaser"));
		sprites.put(Type.ignorant, Utilities.pathLoad(app, "ignorant"));
		sprites.put(Type.whim, Utilities.pathLoad(app, "whim"));
		clearSprite = Utilities.pathLoad(app, "clearGhost");
	}

	public boolean tic(PApplet app, Player player, int counter)
	{
		List<Direction> validMoves = validDirections();

		validMoves.sort(
			(a, b) -> {
				Point target = type.target.at(player, getPoint());
				return Math.abs(translate(a,1).distance(target))
							 - Math.abs(translate(b,1).distance(target));
			}
		);

		direction = validMoves.get(0);
		Point point = translate(direction, 1);
		this.x = point.x;
		this.y = point.y;

		app.image(getSprite(), displayX(), displayY());

		return player.getPoint().distance(this.getPoint()) < 10;
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
		return Ghost.sprites.get(type);
	}
}
