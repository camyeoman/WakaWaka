package ghost;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Arrays;

import processing.core.PImage;
import processing.core.PApplet;

public class Ghost extends Agent {
	static Map<Type, PImage> sprites;
	private Type type;

	enum Type {
		ambusher,
		chaser,
		ignorant,
		whim;
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
	}

	public boolean tic(PApplet app, Player player, int counter)
	{
		app.image(getSprite(), displayX(), displayY());
		/*
		if ( Utilities.distance(new int[]{x, y}, player.getCoords()) < 8 ) {
			return false;
		} else {
			//setDirection(player);
			move();
			return true;
		}
		*/
		return true;
	}

	public List<Direction> validDirections()
	{
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

	public void setDirection(Direction p)
	{
		/*
		if (p == null || p.getDirection() == null) {
			return;
		}

		int[] target = p.nextCoords(p.getDirection(),8);
		Comparator<Direction> ambusher
				= (a, b) -> (int)( distance(a, target) - distance(b, target) );
		List<Direction> valid = validDirections();
		if (valid.size() > 0) {
			//valid.sort(ambusher);
			this.direction = valid.get(0);
		}
		*/
	}

	public Double distance(Player player)
	{
		/*
		if (direction == null) {
			return null;
		}

		int[] ghostCoords = nextCoords(direction, 1);
		int[] playerCoords = player.nextCoords(player.getDirection(), 8);

		return Utilities.distance(ghostCoords, playerCoords);
		*/
		return 0.2;
	}

	public Double distance(Direction direction, int[] coords)
	{
		/*
		if (direction == null || coords == null) {
			return null;
		}

		int[] p = nextCoords(direction, 1);
		return Utilities.distance(p, coords);
		*/
		return 0.2;
	}
}
