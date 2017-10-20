package pbt.demo;

import net.jqwik.api.*;
import net.jqwik.properties.*;
import org.assertj.core.api.Assertions;

import static org.assertj.core.data.Percentage.*;

class Demo2Properties {

	@Property(shrinking = ShrinkingMode.OFF)
	void squareOfRootIsOriginalValue(@ForAll double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(10));
	}

	@Property(shrinking = ShrinkingMode.OFF)
	void squareOfRootIsOriginalValue1(
			@ForAll @DoubleRange(min = 0, max = Double.MAX_VALUE) double aNumber
	) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(1));
	}

	@Property(shrinking = ShrinkingMode.OFF)
	void squareOfRootIsOriginalValue2(@ForAll("positiveDouble") double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(1));
	}

	@Generate
	Arbitrary<Double> positiveDouble() {
		return Arbitraries.doubles().filter(aDouble -> aDouble > 0);
	}

	@Property(shrinking = ShrinkingMode.OFF)
	void squareOfRootIsOriginalValue3(@ForAll double aNumber) {
		Assume.that(aNumber > 0);
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(1));
	}

}
