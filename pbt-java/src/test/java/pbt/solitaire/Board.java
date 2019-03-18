package pbt.solitaire;

public class Board {
	private final int size;

	public Board(int size) {
		if (size < 1 || size % 2 == 0)
			throw new IllegalArgumentException("Only boards of odd size >= 1 allowed");
		this.size = size;
	}

	public Hole hole(int x, int y) {
		if (x == center() && y == center())
			return Hole.EMPTY;
		return Hole.PEG;
	}

	private int center() {
		return size / 2 + 1;
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return String.format("Board(%s)", size);
	}
}