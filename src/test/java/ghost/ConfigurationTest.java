package ghost;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationTest extends TestTools {

	/**
	 * Test the empty constructor and the specific file constructor.
	 */
	@Test
	public void constructor()
	{
		Configuration config;

		// empty constructor
		config = new Configuration();
		assertNotNull(config);

		// test non-empty constructor
		config = new Configuration("src/test/resources/config1.json");
		assertNotNull(config);

		assertArrayEquals(config.spriteMap, map1);
	}

	/**
	 * Error handling with bad files.
	 */
	@Test
	public void errorHandling() {
		Configuration config = new Configuration("thisFileDoesNotExist");
		assertEquals(config.error, Configuration.Errors.fileNotFound);

		config = new Configuration("src/test/resources/parseException.json");
		assertEquals(config.error, Configuration.Errors.parseException);

		config = new Configuration("src/test/resources/fileNotFoundException.json");
		assertEquals(config.error, Configuration.Errors.fileNotFound);
	}

}
