package pbt.demos;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class Demo1Properties {

	@Example
	void reverseList() {
		List<Integer> aList = Arrays.asList(1, 2, 3);
		Collections.reverse(aList);
		assertThat(aList).containsExactly(3, 2, 1);
	}

	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> original) {
		List<Integer> copy = new ArrayList<>(original);
		Collections.reverse(copy);
		Collections.reverse(copy);
		return copy.equals(original);
	}

	@Property
	void lengthOfStringAlwaysPositive(@ForAll String aString) {
		assertThat(aString.length()).isGreaterThanOrEqualTo(0);
	}

	@Property
	boolean absoluteValueOfIntegerAlwaysPositive(@ForAll int anInteger) {
		return Math.abs(anInteger) >= 0;
	}

	@Property
	boolean sumOfTwoIntegersAlwaysGreaterThanEach(
			@ForAll int positive1, //
			@ForAll int positive2
	) {
		int sum = positive1 + positive2;
		return sum > positive1 && sum > positive2;
	}
}
