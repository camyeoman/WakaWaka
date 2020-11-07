package ghost;

import processing.core.PImage;
import java.util.*;

public class GameObject {
	public static List<GameObject> objects = new ArrayList<>();
	static Map<Type, PImage> sprites = new HashMap<>();
	public final Type type;
	private int x, y;

	public int getX() { return x; }
	public int getY() { return y; }

	public float displayX() { return (float)(x - 6); }
	public float displayY() { return (float)(y - 6); }

	public Point getPoint() { return new Point(x, y); }

	public GameObject(Type type, int x, int y) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public PImage getSprite() {
		return sprites.get(type);
	}

	enum Type {
		fruit
	}

	public static boolean ticAll(App app, Player player) {
		// Draw GameObjects
		for (int i=0; i < objects.size(); i++) {
			GameObject obj = objects.get(i);

			if (obj.getPoint().distance(player.getPoint()) < 1) {
				player.points++;
				objects.remove(i--);
			} else {
				app.image(obj.getSprite(), obj.getX(), obj.getY());
			}
		}
		
		return objects.size() > 0;
	}

	public String toString() {
		return String.format("( %s, %s )",this.x,this.y);
	}
}
