package pbt.stateful.stack;

import java.util.*;

public class MyStringStack {
	private final List<String> elements = new ArrayList<>();

	public void push(String element) {
		elements.add(0, element);
	}

	public String pop() {
		return elements.remove(0);
	}

	public void clear() {
		//if (elements.size() < 3) elements.clear();
		//elements.clear();
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public int size() {
		return elements.size();
	}

	public String top() {
		return elements.get(0);
	}

	@Override
	public String toString() {
		return elements.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MyStringStack that = (MyStringStack) o;

		return elements.equals(that.elements);
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}
}
