package pbt.oracle.javamagazine;

import java.util.*;

public class Aggregator {

	private final HashMap<Integer, Integer> tally = new HashMap<>();

	public void receive(int measurement) {
		int previousCount = tally.getOrDefault(measurement, 0);
		tally.put(measurement, previousCount + 1);
	}

	public Map<Integer, Integer> tally() {
		return tally;
	}
}
