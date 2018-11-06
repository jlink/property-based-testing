package pbt.oracle.javamagazine;

import java.util.*;

import org.junit.jupiter.api.*;

class AggregatorTests {

	@Test
	void tallyOfSeveralValues() {
		Aggregator aggregator = new Aggregator();
		aggregator.receive(1);
		aggregator.receive(2);
		aggregator.receive(3);
		aggregator.receive(2);

		Map<Integer, Integer> tally = aggregator.tally();
		Assertions.assertEquals(1, (int) tally.get(1));
		Assertions.assertEquals(2, (int) tally.get(2));
		Assertions.assertEquals(3, (int) tally.get(1));
	}
}
