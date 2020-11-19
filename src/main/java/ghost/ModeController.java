package ghost;
import java.util.List;
import java.util.ArrayList;

public class ModeController
{
	private List<Node> modeQueue;
	final List<Integer> modeLengths;

	public ModeController(List<Integer> modeLengths) {
		this.modeLengths = modeLengths;
		buildQueue();
	}

	class Node {
		private int timeLeft;
		final Ghost.Mode mode;

		public Node(int duration, Ghost.Mode mode) {
			this.timeLeft = duration;
			this.mode = mode;
		}

		public boolean tic() {
			timeLeft -= 1;
			return timeLeft == 0;
		}
	}

	public void buildQueue() {
		for (int i=0; i < modeLengths.size(); i++) {
			Ghost.Mode mode = (i % 2 == 0) ? Ghost.Mode.SCATTER : Ghost.Mode.CHASE;
			addMode(modeLengths.get(i), mode);
		}
	}

	public void addMode(int n, Ghost.Mode mode) {
		if (mode == Ghost.Mode.FRIGHTENED) {
			// add to start of queue
			modeQueue.add(0, new Node(n, mode));
		} else {
			// add to end of queue
			modeQueue.add(new Node(n, mode));
		}
	}

	public Ghost.Mode mode() {
		if (modeQueue.size() == 0) {
			buildQueue();
			return mode();
		}

		Node node = modeQueue.get(0);

		if (!node.tic()) {
			modeQueue.remove(0);
		}

		return node.mode;
	}
}
