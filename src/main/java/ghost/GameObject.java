package ghost;

import processing.core.PImage;
import java.util.*;

public class GameObject {
	static Map<Types, PImage> sprites = new HashMap<>();
	public final Types type;
	private int x, y;

	public int getX() { return x; }
	public int getY() { return y; }

	public float displayX() { return (float)(x - 6); }
	public float displayY() { return (float)(y - 6); }

	public GameObject(Types type, int x, int y) {
		this.x = x;
		this.y = y;
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
