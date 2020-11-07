package ghost;

import processing.core.PImage;
import java.util.*;

public class GameObject {
	private static Map<Type, PImage> sprites;
	private Type type;
	private int x, y;

	public int getX() { return x; }
	public int getY() { return y; }

	public float displayX() { return (float)(x - 6); }
	public float displayY() { return (float)(y - 6); }

	public Point getPoint() { return new Point(x, y); }

	public static void setUp(Game game) {
		sprites = game.gameSprites;
	}

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

	public void draw(App app) {
		// Draw GameObjects
		app.image(getSprite(), x, y);
	}

	public String toString() {
		return String.format("( %s, %s )",this.x,this.y);
	}
}
