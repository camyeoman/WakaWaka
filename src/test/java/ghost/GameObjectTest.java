package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class GameObjectTest {
	@Test
	public void constructor() {
		GameObject obj;


		for (GameObject.Type type : GameObject.Type.values()) {
			obj = new GameObject(0, 0, type.sprite);
			assertNotNull(obj);
			assertEquals(obj.type, type);
		}

	}
}
