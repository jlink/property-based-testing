package pbt.demos;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.assertj.core.api.Assertions;

import static org.assertj.core.data.Percentage.*;

class ConstrainingValuesExamples {

	// Expected to fail since there is no sqrt of negative numbers
	@Property
	void squareOfRootIsOriginalValue(@ForAll double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(10));
	}

	@Property
	void squareOfRootIsOriginalValue_1(@ForAll @Positive double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(1));
	}

	@Property
	void squareOfRootIsOriginalValue_2(@ForAll("positiveDoubles") double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(1));
	}

	@Provide
	Arbitrary<Double> positiveDoubles() {
		return Arbitraries.doubles().filter(aDouble -> aDouble > 0);
	}

	@Property
	void squareOfRootIsOriginalValue_3(@ForAll double aNumber) {
		Assume.that(aNumber > 0);
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, withPercentage(1));
	}

}
