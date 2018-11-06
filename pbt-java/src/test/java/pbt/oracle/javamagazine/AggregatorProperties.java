package pbt.oracle.javamagazine;

import java.util.*;

import net.jqwik.api.*;

class AggregatorProperties {

	@Property
	boolean allMeasuredValuesShowUpAsKeysInTally(@ForAll List<Integer> measurements) {
		Aggregator aggregator = new Aggregator();
		measurements.forEach(aggregator::receive);
		return measurements.stream().allMatch(m -> aggregator.tally().containsKey(m));
	}

	@Property
	void numbersNeverMeasuredDontShowUpInTally(@ForAll List<Integer> measurements) {
	}

	@Property
	boolean sumOfAllCountsIsNumberOfMeasurements(@ForAll List<Integer> measurements) {
		Aggregator aggregator = new Aggregator();
		measurements.forEach(aggregator::receive);
		int sumOfAllCounts = aggregator.tally().values().stream().mapToInt(i -> i).sum();
		return sumOfAllCounts == measurements.size();
	}

	@Property
	void orderOfMeasuringDoesNotChangeTally(@ForAll List<Integer> measurements) {
	}
}
