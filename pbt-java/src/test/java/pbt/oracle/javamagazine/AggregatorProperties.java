package pbt.oracle.javamagazine;

import java.util.*;

import net.jqwik.api.*;

class AggregatorProperties {

	@Property
	void allMeasurementsHaveKeyInTally(@ForAll List<Integer> measurements) {
	}

	@Property
	void numbersNeverMeasuredDontHaveKeyInTally(@ForAll List<Integer> measurements) {
	}

	@Property
	void sumOfAllCountsIsNumberOfMeasurements(@ForAll List<Integer> measurements) {
	}

	@Property
	void orderOfMeasuringDoesNotChangeTally(@ForAll List<Integer> measurements) {
	}
}
