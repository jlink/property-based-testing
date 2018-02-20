package pbt.demos;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class ListReverseProperties {

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

	@Property(reporting = Reporting.GENERATED)
	boolean reverseWithWildcardType(@ForAll List<?> original) {
		List<?> copy = new ArrayList<>(original);
		Collections.reverse(copy);
		Collections.reverse(copy);
		return copy.equals(original);
	}

	@Property(reporting = Reporting.GENERATED)
	<T> boolean reverseWithTypeVariable(@ForAll List<T> original) {
		List<T> copy = new ArrayList<>(original);
		Collections.reverse(copy);
		Collections.reverse(copy);
		return copy.equals(original);
	}


}
