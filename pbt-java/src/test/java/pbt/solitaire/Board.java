package pbt.solitaire;

import java.io.*;
import java.util.*;

public class Board implements Serializable {
	private final int size;
	private List<Hole> holes = new ArrayList<>();

	public Board(int size) {
		if (size < 1 || size % 2 == 0)
			throw new IllegalArgumentException("Only boards of odd size >= 1 allowed");
		this.size = size;
		initHoles(size);
	}

	private void initHoles(int size) {
		for (int i = 0; i < size * size; i++) {
			holes.add(Hole.PEG);
		}
		removePeg(center());
	}

	public Hole hole(Position position) {
		return holes.get(calculateIndex(position));
	}

	public Position center() {
		int centerIndex = size / 2 + 1;
		return Position.xy(centerIndex, centerIndex);
	}

	public boolean isCenter(Position position) {
		return position.equals(center());
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return String.format("Board(%s)", size);
	}

	public void removePeg(Position position) {
		int index = calculateIndex(position);
		holes.set(index, Hole.EMPTY);
	}

	private int calculateIndex(Position position) {
		return (position.x() -1) * size + (position.y() -1);
	}
}
