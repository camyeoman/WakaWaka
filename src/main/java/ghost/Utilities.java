package ghost;

import java.util.regex.*;
import java.util.*;

public class Utilities {
	public static double distance(double[] a, double[] b) {
		int xDist = (int)Math.pow(a[0] - b[0], 2);
		int yDist = (int)Math.pow(a[1] - b[1], 2);
		return (int)Math.pow(xDist + yDist, 0.5);
	}

	public static double round(double n, int places) {
		int pow10 = (int)(Math.pow(10,places-1));
		return (double)Math.round(n * pow10) / pow10;
	}

  public static String[] extractMatches(String regex, String rawString) {
		// returns all the matches of a regular expression
    Matcher match = Pattern.compile(regex).matcher(rawString);
		List<String> matches = new ArrayList<>();
		while (match.find()) matches.add(match.group());

		return matches.toArray(new String[matches.size()]);
  }
}
