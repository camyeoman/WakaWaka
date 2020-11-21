package ghost;

import processing.core.PApplet;

public class GameObject extends Coordinate {
	private Type type;
	private boolean eaten;

	public boolean isEaten() {
		return eaten;
	}

	public GameObject(int x, int y, Sprite sprite) {
		super(x, y);

		if (sprite == null) {
			this.type = null;
		} else {
			switch (sprite) {
				case superFruit:  this.type = Type.superFruit;  break;
				case fruit:       this.type = Type.fruit;       break;
				case soda:        this.type = Type.soda;        break;
			}
		}
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

	public void fruit(Game game) {
		game.points++;
	}

	public void superFruit(Game game) {
		//System.out.println("super fruit");
		game.modeControl.frightened();
		//System.out.println(game.modeControl);
	}

	public void soda(Game game) {
		// TODO
	}

	// Active methods
	public void tic(Game game) {
		if (point().distance(game.PLAYER.point()) < 10) {
			eaten = true;

			switch (type) {
				case superFruit:  superFruit(game);  break;
				case fruit:       fruit(game);       break;
				case soda:        soda(game);        break;
			}
		}
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
