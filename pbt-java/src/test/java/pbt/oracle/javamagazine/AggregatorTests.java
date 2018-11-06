package pbt.oracle.javamagazine;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class AggregatorTests {

	@Example
	void tallyOfSeveralValues() {
		Aggregator aggregator = new Aggregator();
		aggregator.receive(1);
		aggregator.receive(2);
		aggregator.receive(3);
		aggregator.receive(2);

		Map<Integer, Integer> tally = aggregator.tally();
		Assertions.assertThat(tally.get(1)).isEqualTo(1);
		Assertions.assertThat(tally.get(2)).isEqualTo(2);
		Assertions.assertThat(tally.get(3)).isEqualTo(1);
	}
}
