package ghost;
import java.util.List;
import java.util.ArrayList;

public class ModeController
{
	private List<Node> modeQueue;
	final List<Integer> modeLengths;

	public ModeController(List<Integer> modeLengths) {
		this.modeLengths = modeLengths;
		this.modeQueue = new ArrayList<>();
		buildQueue();
	}

	class Node {
		private int framesLeft;
		final Ghost.Mode mode;

		public Node(int duration, Ghost.Mode mode) {
			this.framesLeft = 60 * duration;
			this.mode = mode;
		}

		public boolean tic() {
			framesLeft -= 1;
			return framesLeft > 0;
		}

		public String toString() {
			return framesLeft + " left";
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

	public Ghost.Mode update() {
		if (modeQueue.size() == 0) {
			buildQueue();
			return update();
		}

		Node node = modeQueue.get(0);

		if (!node.tic()) {
			modeQueue.remove(0);
		}

		return node.mode;
	}

	public String toString() {
		return modeQueue.toString();
	}
}
