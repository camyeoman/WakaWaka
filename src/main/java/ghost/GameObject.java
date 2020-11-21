package ghost;

import processing.core.PApplet;

public class GameObject extends Coordinate {
	private boolean eaten;
	final Type type;

	public boolean isEaten() {
		return eaten;
	}

	public GameObject(int x, int y, Sprite sprite) {
		super(x, y);

		Type temp = null;
		if (sprite != null) {
			switch (sprite) {
				case superFruit:  temp = Type.superFruit;  break;
				case fruit:       temp = Type.fruit;       break;
				case soda:        temp = Type.soda;        break;
			}
		}

		this.type = temp;
	}

	public Sprite getSprite() {
		Sprite sprite = null;

		switch (type) {
			case superFruit:  sprite = Sprite.superFruit;  break;
			case fruit:       sprite = Sprite.fruit;       break;
			case soda:        sprite = Sprite.soda;        break;
		}

		return sprite;
	}

	enum Type {
		fruit,
		superFruit,
		soda;
	}

	// collision methods

	public static void superFruit(Game game) {
		game.modeControl.frightened();
	}

	public static void fruit(Game game) {
		game.points++;
	}

	public static void soda(Game game) {
		// TODO
	}

	// Active methods
	public boolean tic(Player PLAYER) {
		if (point().distance(PLAYER.point()) < 10) {
			eaten = true;
			return false;
		}

		return true;
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
