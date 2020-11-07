package ghost;

import java.util.regex.*;
import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Utilities {

	// SETUP FUNCTIONS

	public static double distance(int[] a, int[] b)
	{
		int xDist = (int)Math.pow(a[0] - b[0], 2);
		int yDist = (int)Math.pow(a[1] - b[1], 2);
		return (int)Math.pow(xDist + yDist, 0.5);
	}

	public static double round(double n, int places)
	{
		int pow10 = (int)(Math.pow(10,places-1));
		return (double)Math.round(n * pow10) / pow10;
	}

	public static String[] extractMatches(String regex, String rawString)
	{
		// returns all the matches of a regular expression
		Matcher match = Pattern.compile(regex).matcher(rawString);
		List<String> matches = new ArrayList<>();
		while (match.find()) matches.add(match.group());

		return matches.toArray(new String[matches.size()]);
	}

	public static Point restrictRange(Point point)
	{
		if (point.x < 0 || point.x > 448) {
			point.x = (point.x < 0) ? 0 : 448;
		}

		if (point.y < 0 || point.y > 576) {
			point.x = (point.x < 0) ? 0 : 448;
		}

		return point;
	}
}
