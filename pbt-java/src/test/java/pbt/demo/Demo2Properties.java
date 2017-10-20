package pbt.demo;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;

class Demo2Properties {

	@Property
	void squareOfRootIsOriginalValue(@ForAll double aNumber) {
		double sqrt = Math.sqrt(aNumber);
		Assertions.assertThat(sqrt * sqrt).isCloseTo(aNumber, Percentage.withPercentage(10));
	}
}
