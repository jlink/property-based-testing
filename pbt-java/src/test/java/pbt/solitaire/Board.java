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
		removePeg(center(), center());
	}

	public Hole hole(int x, int y) {
		return holes.get(calculateIndex(x, y));
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

	public void removePeg(int x, int y) {
		int index = calculateIndex(x, y);
		holes.set(index, Hole.EMPTY);
	}

	private int calculateIndex(int x, int y) {
		return (x-1) * size + (y -1);
	}
}
