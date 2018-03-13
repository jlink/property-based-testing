package pbt.stateful;

import java.util.*;

public class MyStringStack {
	private List<String> elements = new ArrayList<>();

	public void push(String element) {
		elements.add(0, element);
	}

	public String pop() {
		return elements.remove(0);
	}

	public void clear() {

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
}
