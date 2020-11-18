package ghost;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Arrays;
import java.util.stream.Collectors;

import processing.core.PImage;
import processing.core.PApplet;

public enum Sprite {
	// player sprites
	playerUp,
	playerDown,
	playerLeft,
	playerRight,
	playerClosed,

	// ghost sprites
	ghostAmbusher,
	ghostIgnorant,
	ghostChaser,
	ghostWhim,
	ghostFrightened,

	// game objects
	fruit,
	superFruit,

	// walls
	horizontal,
	vertical,
	upLeft,
	upRight,
	downLeft,
	downRight;

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
			case "a":  sprite = Sprite.ghostAmbusher;  break;
			case "i":  sprite = Sprite.ghostIgnorant;  break;
			case "c":  sprite = Sprite.ghostChaser;    break;
			case "w":  sprite = Sprite.ghostWhim;      break;
			case "p":  sprite = Sprite.playerRight;    break;
		}

		return sprite;
	}

	public boolean isGhost() {
		boolean ghost = false;
		switch (this) {
			case ghostAmbusher:  ghost = true;  break;
			case ghostIgnorant:  ghost = true;  break;
			case ghostChaser:    ghost = true;  break;
			case ghostWhim:      ghost = true;  break;
		}

		return ghost;
	}

	public boolean isWall() {
		boolean wall = false;
		switch (this) {
			case horizontal:  wall = true;  break;
			case vertical:    wall = true;  break;
			case upLeft:      wall = true;  break;
			case upRight:     wall = true;  break;
			case downLeft:    wall = true;  break;
			case downRight:   wall = true;  break;
		}

		return wall;
	}
}
