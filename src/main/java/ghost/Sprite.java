package ghost;

/**
 * Reprents the image of an object.
 */
public enum Sprite {

	// player sprites
	playerClosed("playerClosed", "player"),
	playerRight("playerRight",   "player"),
	playerLeft("playerLeft",     "player"),
	playerDown("playerDown",     "player"),
	playerUp("playerUp",         "player"),

	// ghost sprites
	ambusher("ambusher",         "ghost"),
	ignorant("ignorant",         "ghost"),
	chaser("chaser",             "ghost"),
	whim("whim",                 "ghost"),
                             
	// walls
	horizontal("horizontal",     "wall"),
	downRight("downRight",       "wall"),
	downLeft("downLeft",         "wall"),
	vertical("vertical",         "wall"),
	upRight("upRight",           "wall"),
	upLeft("upLeft",             "wall"),

	// game objects
	superFruit("superFruit",     "gameObject"),
	fruit("fruit",               "gameObject"),
	soda("soda",                 "gameObject"),
                               
	// altered state ghosts      
	frightened("frightened",     "alteredGhost"),
	invisible("invisible",       "alteredGhost");

	/**
	 * Catagory of sprite.
	 */
	private final String type;

	/**
	 * File path to sprite.
	 */
	final String filePath;

	/**
	 * Initialise file path and sprite.
	 * @param name, the name of the image
	 * @param type, the catagory of sprite
	 */
	Sprite(String name, String type) {
		this.filePath = "src/main/resources/" + name + ".png";
		this.type = type;
	}

	/**
	 * Return sprite associated with map character.
	 * @param character, one letter string, the map character
	 * @return sprite object associated with map character
	 */
	public static Sprite getSprite(String character) {

		Sprite sprite = null;

		switch (character) {
			case "1":
				sprite = Sprite.horizontal;
				break;
			case "2":
				sprite = Sprite.vertical;
				break;
			case "3":
				sprite = Sprite.upLeft;
				break;
			case "4":
				sprite = Sprite.upRight;
				break;
			case "5":
				sprite = Sprite.downLeft;
				break;
			case "6":
				sprite = Sprite.downRight;
				break;
			case "7":
				sprite = Sprite.fruit;
				break;
			case "8":
				sprite = Sprite.superFruit;
				break;
			case "9":
				sprite = Sprite.soda;
				break;
			case "a":
				sprite = Sprite.ambusher;
				break;
			case "i":
				sprite = Sprite.ignorant;
				break;
			case "c":
				sprite = Sprite.chaser;
				break;
			case "w":
				sprite = Sprite.whim;
				break;
			case "p":
				sprite = Sprite.playerRight;
				break;
		}

		return sprite;

	}

	/**
	 * Returns if a sprite is of catagory ghost.
	 * @return if sprite represents ghost
	 */
	public boolean isGhost() {
		return type.equals("ghost");
	}

	/**
	 * Returns if a sprite is of catagory gameObject.
	 * @return if sprite represents gameObject
	 */
	public boolean isGameObject() {
		return type.equals("gameObject");
	}

	/**
	 * Returns if a sprite is of catagory wall.
	 * @return if sprite represents wall
	 */
	public boolean isWall() {
		return type.equals("wall");
	}

	/**
	 * Returns if a sprite is occupiable by a player character.
	 * @param sprite, sprite to be checked
	 * @return if sprite is occupiable
	 */
	public static boolean occupiable(Sprite sprite) {
		return sprite == null || !sprite.isWall();
	}

}
