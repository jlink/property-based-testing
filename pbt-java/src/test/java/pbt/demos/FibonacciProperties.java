package pbt.demos;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;

import static org.assertj.core.api.Assertions.*;

class FibonacciProperties {

	@Example
	void fib5() {
		assertThat(fib(7)).isEqualTo(13);
	}

	@Property
	@FromData("fibs")
	void fibExamples(@ForAll int fibIndex, @ForAll int fibNumber) {
		assertThat(fib(fibIndex)).isEqualTo(fibNumber);
	}

	@Data
	Iterable<Tuple.Tuple2<Integer, Integer>> fibs() {
		return Table.of(
			Tuple.of(0, 0),
			Tuple.of(1, 1),
			Tuple.of(2, 1),
			Tuple.of(3, 2),
			Tuple.of(4, 3),
			Tuple.of(5, 5),
			Tuple.of(6, 8)
		);
	}

	@Property
	void fib_is_sum_of_preceding_fibs(@ForAll @IntRange(min = 2, max = 42) int fibIndex) {
		//System.out.println(fibIndex);
		assertThat(fib(fibIndex))
			.isEqualTo(fib(fibIndex - 1) + fib(fibIndex - 2));
	}

	@Property
	void fib_can_calculate_large_numbers(@ForAll @IntRange(min = 2, max = 1946) int fibIndex) {
		assertThat(fib(fibIndex))
			.isEqualTo(fib(fibIndex - 1) + fib(fibIndex - 2));
	}

	@Property
	// @Report(Reporting.GENERATED)
	@StatisticsReport(label = "distribution", format = NumberRangeHistogram.class)
	void fib_can_calculate_large_numbers2(@ForAll("possibleFibs") int fibIndex) {
		assertThat(fib(fibIndex))
			.isEqualTo(fib(fibIndex - 1) + fib(fibIndex - 2));

		Statistics.label("distribution").collect(fibIndex);
		Statistics.label("even")
				  .collect(fibIndex % 2 == 0)
				  .coverage(checker -> checker.check(true).percentage(p -> p > 40 && p < 60));
	}

	@Provide
	Arbitrary<Integer> possibleFibs() {
		return Arbitraries.integers().between(2, 1500)
						  .withDistribution(RandomDistribution.uniform())
						  //.withDistribution(RandomDistribution.gaussian())
						  //.filter(i -> i % 2 == 0)
						  .withoutEdgeCases();
	}

	static Map<Integer, Integer> memoizedFibs = new HashMap<>();

	static int fib(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("Fibonacci index must be >= 0");
		}
		return memoizedFibs.computeIfAbsent(n, index -> calculateFib(index));
	}

	private static int calculateFib(int n) {
		if (n == 0) return 0;
		else if (n == 1) return 1;
		else return fib(n - 1) + fib(n - 2);
	}

}
