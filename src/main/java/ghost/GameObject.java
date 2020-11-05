package ghost;

import processing.core.PImage;
import java.util.*;

public class GameObject implements Coordinates {
	static Map<Types, PImage> sprites = new HashMap<>();
	public final Types type;
	private double x, y;

	public float getX() { return (float)x; }
	public float getY() { return (float)y; }

	public GameObject(Types type, int x, int y) {
		this.x = 16 * x;
		this.y = 16 * y;
		this.type = type;
	}

	public PImage getSprite() {
		return sprites.get(type);
	}

	enum Types {
		fruit
	}

	public boolean tic(Player player) {
		if (Math.abs(x - player.getX()) < 10 && Math.abs(y - player.getY()) < 10) {
			return false;
		} else {
			return true;
		}
	}

	public String toString() {
		return String.format("( %s, %s )",this.x,this.y);
	}
}
