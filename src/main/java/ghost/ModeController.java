package ghost;
import java.util.List;
import java.util.ArrayList;

public class ModeController {
	private List<Node> modeQueue;
	final List<Integer> modeLengths;

	final int frightenedDuration;
	final int invisibleDuration;

	/**
	* Initialises an ModeController with a modeLengths and frightened Duration
	* using a configuration object.
	* @param config, configuration obejct
	*/
	public ModeController(Configuration config) {
		this.frightenedDuration = config.frightenedDuration;
		this.invisibleDuration = 10;

		this.modeLengths = config.modeLengths;
		this.modeQueue = new ArrayList<>();

		buildQueue();
	}

	/**
	 * Initialises mode queue using the modeLengths given in the constructor.
	 */
	public void buildQueue() {
		for (int i=0; i < modeLengths.size(); i++) {
			Ghost.Mode mode = (i % 2 == 0) ? Ghost.Mode.SCATTER : Ghost.Mode.CHASE;
			modeQueue.add(new Node(modeLengths.get(i), mode));
		}
	}

	/**
	 * Adds frightened mode to the beginning of the queue with duration as
	 * specified in the constructor with the frightenedDuration attribute.
	 */
	public void queueMode(Ghost.Mode mode) {
		if (mode == Ghost.Mode.FRIGHTENED) {
			modeQueue.add(0, new Node(frightenedDuration, mode));
		} else if (mode == Ghost.Mode.INVISIBLE) {
			modeQueue.add(0, new Node(invisibleDuration, mode));
		}
	}


	/**
	 * Updates the mode que, representing a single frame having elapsed.
	 * When the que is empty, rebuild it, and if a given mode is over remove
	 * it from the queue. Return the current mode.
	 * @return the current mode of the game
	 */
	public Ghost.Mode update() {
		Node node = modeQueue.get(0);

		if (!node.tic()) {
			modeQueue.remove(0);

			if (modeQueue.size() == 0) {
				buildQueue();
			}
		}

		return node.mode;
	}

	/**
	 * Returns a string that represents the current state of the mode queue.
	 * @return string representation of que
	 */
	public String toString() {
		return modeQueue.toString();
	}

	class Node {
		private int framesLeft;
		final Ghost.Mode mode;

		/**
		* Initialises an mode Node with the specified mode and duration.
		* @param duration, the duration of the mode in seconds
		* @param mode, the mode the game will be in
		*/
		public Node(int duration, Ghost.Mode mode) {
			this.framesLeft = 60 * duration;
			this.mode = mode;
		}

		/**
		 * Reduces the framesLeft attribute by one, if not greater than zero
		 * then return false. When a node returns false it is removed from the
		 * queue in the update() function.
		 * @return whether the mode is still active
		 */
		public boolean tic() {
			framesLeft -= 1;
			return framesLeft > 0;
		}

		/**
		 * Returns a string showing the remaining duration of the Node.
		 * @return string showing frames left of mode
		 */
		public String toString() {
			return framesLeft + " " + mode;
		}
	}
}
