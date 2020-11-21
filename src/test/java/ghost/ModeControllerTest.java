package ghost;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ModeControllerTest extends TestTools {

	public static ModeController getController(Integer[] lengths, int duration) {
		Configuration config = new Configuration();
		config.modeLengths = Arrays.stream(lengths).collect(Collectors.toList());
		config.frightenedDuration = duration;

		return new ModeController(config);
	}


	@Test
	public void constructor()
	{
		List<Integer> lengths = Arrays.stream(new Integer[]{1,2,3,4,5})
			.collect(Collectors.toList());

		ModeController ctrl = getController(new Integer[]{1,2,3,4,5}, 5);

		assertNotNull(ctrl);
		assertEquals(ctrl.modeLengths, lengths);
		assertEquals(ctrl.frightenedDuration, 5);
		String str = "[60 SCATTER, 120 CHASE, 180 SCATTER, "
			+ "240 CHASE, 300 SCATTER]";
		assertEquals(ctrl.toString(), str);
	}

	@Test
	public void generalUsageTest()
	{
		String str, newStr, formatStr;

		ModeController ctrl = getController(new Integer[]{7,20,7,20}, 5);
		str = "[420 SCATTER, 1200 CHASE, 420 SCATTER, 1200 CHASE]";

		formatStr = "[%s SCATTER, 1200 CHASE, 420 SCATTER, 1200 CHASE]";
		for (int i=1; true; i++) {
			assertEquals(ctrl.update(), Ghost.Mode.SCATTER);
			if (i < 420) {
				assertEquals(ctrl.toString(), String.format(formatStr, 420-i));
			} else {
				assertEquals(ctrl.toString(), "[1200 CHASE, 420 SCATTER, 1200 CHASE]");
				break;
			}
		}

		formatStr = "[%s CHASE, 420 SCATTER, 1200 CHASE]";
		for (int i=1; true; i++) {
			assertEquals(ctrl.update(), Ghost.Mode.CHASE);
			if (i < 1200) {
				assertEquals(ctrl.toString(), String.format(formatStr, 1200-i));
			} else {
				assertEquals(ctrl.toString(), "[420 SCATTER, 1200 CHASE]");
				break;
			}
		}

		formatStr = "[%s SCATTER, 1200 CHASE]";
		for (int i=1; true; i++) {
			assertEquals(ctrl.update(), Ghost.Mode.SCATTER);
			if (i < 420) {
				assertEquals(ctrl.toString(), String.format(formatStr, 420-i));
			} else {
				assertEquals(ctrl.toString(), "[1200 CHASE]");
				break;
			}
		}

		formatStr = "[%s CHASE]";
		for (int i=1; true; i++) {
			assertEquals(ctrl.update(), Ghost.Mode.CHASE);
			if (i < 1200) {
				assertEquals(ctrl.toString(), String.format(formatStr, 1200-i));
			} else {
				assertEquals(ctrl.toString(), str);
				break;
			}
		}
	}

	@Test
	public void frightenedTest()
	{
		String str, newStr, formatStr;

		ModeController ctrl = getController(new Integer[]{7,20}, 5);

		for (int i=1; i <= 100; i++) {
			assertEquals(ctrl.update(), Ghost.Mode.SCATTER);
		}

		// current state
		str = "[320 SCATTER, 1200 CHASE]";
		assertEquals(ctrl.toString(), str);

		ctrl.frightened();

		formatStr = "[%s FRIGHTENED, 320 SCATTER, 1200 CHASE]";
		// currently is [300 CHASE, 320 SCATTER, 1200 CHASE]
		for (int i=0; true; i++) {
			if (i > 0) {
				assertEquals(ctrl.update(), Ghost.Mode.FRIGHTENED);
			}

			if (i < 300) {
				assertEquals(ctrl.toString(), String.format(formatStr, 300-i));
			} else {
				assertEquals(ctrl.toString(), str);
				break;
			}
		}
	}
}
