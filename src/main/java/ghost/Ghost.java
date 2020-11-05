package ghost;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;

import processing.core.PImage;
import processing.core.PApplet;

public class Ghost extends Agent {
	static PImage sprite;

	public Ghost(int x, int y, boolean[][] map)
	{
		super(x, y, map);
		this.direction = null;
	}

	public static void setUp(PApplet app) {

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
		return Ghost.sprite;
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
		if (direction == null) {
			return null;
		}

		int[] ghostCoords = nextCoords(direction, 1);
		int[] playerCoords = player.nextCoords(player.getDirection(), 8);

		return Utilities.distance(ghostCoords, playerCoords);
	}

	public Double distance(Direction direction, int[] coords)
	{
		if (direction == null || coords == null) {
			return null;
		}

		int[] p = nextCoords(direction, 1);
		return Utilities.distance(p, coords);
	}
}
