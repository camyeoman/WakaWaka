package ghost;

import processing.core.PApplet;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
		return (type == null) ? null : type.sprite;
	}

	public void reset() {
		this.eaten = false;
	}

	// collision methods

	public static void superFruit(Game game) {
		game.modeControl.queueMode(Ghost.Mode.FRIGHTENED);
	}

	public static void fruit(Game game) {
		game.points++;
	}

	public static void soda(Game game) {
		game.modeControl.queueMode(Ghost.Mode.INVISIBLE);
	}

	// Active methods

	public static void TIC(Game game) {
		for (GameObject obj : game.GAME_OBJECTS) {

			if (!obj.isEaten() && !obj.evolve(game.PLAYER)) {
				switch (obj.type) {
					case superFruit:
						superFruit(game);
						break;
					case fruit:
						fruit(game);
						break;
					case soda:
						soda(game);
						break;
				}
			}

		}
	}

	public boolean evolve(Player PLAYER) {
		if (point().distance(PLAYER.point()) < 10) {
			eaten = true;
			return false;
		}

		return true;
	}

	public void draw(Game game) {
		// Draw GameObjects
		game.draw(getSprite(), x, y, null);
	}

	enum Type {
		fruit(Sprite.fruit),
		superFruit(Sprite.superFruit),
		soda(Sprite.soda);

		Sprite sprite;

		Type(Sprite sprite) {
			this.sprite = sprite;
		}
	}
}
