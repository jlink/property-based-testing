package pbt.solitaire;

public class Board {
	private final int size;

	public Board(int size) {
		if (size < 1 || size % 2 == 0)
			throw new IllegalArgumentException("Only boards of odd size >= 1 allowed");
		this.size = size;
	}

	public Hole hole(int x, int y) {
		if (x == 3 && y == 3)
			return Hole.EMPTY;
		return Hole.PEG;
	}

	public int size() {
		return size;
	}
}
