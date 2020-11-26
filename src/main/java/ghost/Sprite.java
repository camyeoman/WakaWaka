package ghost;

public enum Sprite {
	// player sprites
	playerClosed("playerClosed",    "player"),
	playerRight("playerRight",      "player"),
	playerLeft("playerLeft",        "player"),
	playerDown("playerDown",        "player"),
	playerUp("playerUp",            "player"),

	// ghost sprites
	ambusher("ambusher",      "ghost"),
	ignorant("ignorant",      "ghost"),
	chaser("chaser",          "ghost"),
	whim("whim",              "ghost"),

	// walls
	horizontal("horizontal",  "wall"),
	downRight("downRight",    "wall"),
	downLeft("downLeft",      "wall"),
	vertical("vertical",      "wall"),
	upRight("upRight",        "wall"),
	upLeft("upLeft",          "wall"),

	// game objects
	superFruit("superFruit",  "gameObject"),
	fruit("fruit",            "gameObject"),
	soda("soda",              "gameObject"),

	// altered state ghosts
	frightened("frightened",  "alteredGhost"),
	invisible("invisible",  "alteredGhost");

	private final String type;
	final String filePath;

	Sprite(String name, String type) {
		this.filePath = "src/main/resources/" + name + ".png";
		this.type = type;
	}

	public static Sprite getSprite(String character) {
		Sprite sprite = null;

		switch (character) {
			case "1":  sprite = Sprite.horizontal;     break;
			case "2":  sprite = Sprite.vertical;       break;
			case "3":  sprite = Sprite.upLeft;         break;
			case "4":  sprite = Sprite.upRight;        break;
			case "5":  sprite = Sprite.downLeft;       break;
			case "6":  sprite = Sprite.downRight;      break;
			case "7":  sprite = Sprite.fruit;          break;
			case "8":  sprite = Sprite.superFruit;     break;
			case "9":  sprite = Sprite.soda;           break;
			case "a":  sprite = Sprite.ambusher;       break;
			case "i":  sprite = Sprite.ignorant;       break;
			case "c":  sprite = Sprite.chaser;         break;
			case "w":  sprite = Sprite.whim;           break;
			case "p":  sprite = Sprite.playerRight;    break;
		}

		return sprite;
	}

	public boolean isGhost() {
		return type.equals("ghost");
	}

	public boolean isGameObject() {
		return type.equals("gameObject");
	}

	public boolean isWall() {
		return type.equals("wall");
	}
}
