package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ConfigurationTest extends TestTools {
	@Test
	public void constructor()
	{
		Configuration config;

		// test non-empty constructor
		config = new Configuration("src/test/resources/config1.json");
		assertNotNull(config);

		assertArrayEquals(config.spriteMap, map1);
	}

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
