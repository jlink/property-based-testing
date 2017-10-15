package pbt.demo;

import static java.util.Collections.*;

import java.util.*;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

class Demo1Properties {

	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> aList) {
		// System.out.println(aList);
		List<Integer> copy = new ArrayList<>(aList);
		reverse(copy);
		reverse(copy);
		return copy.equals(aList);
	}

	@Property
	void lengthOfStringAlwaysPositive(@ForAll String aString) {
		Assertions.assertThat(aString.length()).isGreaterThanOrEqualTo(0);
	}

	@Property
	boolean absoluteValueOfIntegerAlwaysPositive(@ForAll int anInteger) {
		return Math.abs(anInteger) >= 0;
	}

	@Property
	boolean sumOfTwoIntegersAlwaysGreaterThanEach(
		@ForAll int positive1,
		@ForAll int positive2
	) {
		int sum = positive1 + positive2;
		return sum >= positive1 && sum >= positive2;
	}
}
