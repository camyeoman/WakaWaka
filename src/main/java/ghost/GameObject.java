package ghost;

import processing.core.PImage;
import java.util.*;

public class GameObject {
	private Type type;
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

	public Sprite getSprite() {
		Sprite sprite = null;

		switch (type) {
			case fruit:       sprite = Sprite.playerRight;  break;
			case superFruit:  sprite = Sprite.playerLeft;   break;
		}

		return sprite;
	}

	enum Type {
		fruit,
		superFruit;
	}

	public void draw(Game game) {
		// Draw GameObjects
		App app = game.app;
		app.image(game.allSprites.get(getSprite()), x, y);
	}

	public String toString() {
		return String.format("( %s, %s )",this.x,this.y);
	}
}
