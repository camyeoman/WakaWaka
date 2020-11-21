package ghost;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

public class ModeControllerTest extends TestTools {

	public static ModeController getController(List<Integer> modeLengths,
			int frightenedDuration) {
		Configuration config = new Configuration();
		config.modeLengths = modeLengths;
		config.frightenedDuration = frightenedDuration;

		return new ModeController(config);
	}


	@Test
	public void constructor()
	{
		List<Integer> modeLengths = new ArrayList<>();
		for (int i=0; i < 5; i++) {
			modeLengths.add(i);
		}

		ModeController ctrl = getController(modeLengths, 5);

		assertNotNull(ctrl);
		assertEquals(ctrl.modeLengths, modeLengths);
		assertEquals(ctrl.frightenedDuration, 5);
		System.out.println(ctrl);
	}

	@Test
	public void distance()
	{
	}

	@Test
	public void testToString()
	{
	}

}
