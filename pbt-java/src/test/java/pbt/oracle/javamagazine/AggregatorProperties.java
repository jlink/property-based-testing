package pbt.oracle.javamagazine;

import java.util.*;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;

class AggregatorProperties {

	@Property
	boolean allMeasuredValuesShowUpAsKeysInTally(@ForAll List<Integer> measurements) {
		Aggregator aggregator = new Aggregator();
		measurements.forEach(aggregator::receive);
		return measurements.stream().allMatch(m -> aggregator.tally().containsKey(m));
	}

	@Property
	boolean numbersNeverMeasuredDontShowUpInTally(
			@ForAll List<Integer> measured,
			@ForAll Set<Integer> notMeasured
	) {
		notMeasured.removeAll(measured);

		Aggregator aggregator = new Aggregator();
		measured.forEach(aggregator::receive);
		return notMeasured.stream().noneMatch(m -> aggregator.tally().containsKey(m));
	}

	@Property
	boolean sumOfAllCountsIsNumberOfMeasurements(@ForAll List<Integer> measurements) {
		Aggregator aggregator = new Aggregator();
		measurements.forEach(aggregator::receive);
		int sumOfAllCounts = aggregator.tally().values().stream().mapToInt(i -> i).sum();
		return sumOfAllCounts == measurements.size();
	}

	@Property
	void orderOfMeasuringDoesNotChangeTally(
			@ForAll List<Integer> measurements,
			@ForAll Random random
	) {

		Aggregator aggregator1 = new Aggregator();
		measurements.forEach(aggregator1::receive);
		Map<Integer, Integer> tally1 = aggregator1.tally();

		Collections.shuffle(measurements, random);
		Aggregator aggregator2 = new Aggregator();
		measurements.forEach(aggregator2::receive);
		Map<Integer, Integer> tally2 = aggregator2.tally();

		Assertions.assertEquals(tally1, tally2);
	}
}
