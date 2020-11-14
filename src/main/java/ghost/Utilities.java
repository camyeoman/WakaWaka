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

	public static String[] extractMatches(String regex, String rawString)
	{
		// returns all the matches of a regular expression
		Matcher match = Pattern.compile(regex).matcher(rawString);
		List<String> matches = new ArrayList<>();
		while (match.find()) matches.add(match.group());

		return matches.toArray(new String[matches.size()]);
	}

}
