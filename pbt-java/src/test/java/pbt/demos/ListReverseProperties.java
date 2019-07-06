package pbt.demos;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class ListReverseProperties {

	private <T> List<T> reverse(List<T> original) {
		List<T> clone = new ArrayList<>(original);
		// Should produce failing properties:
		// List<T> clone = new ArrayList<>(new HashSet<>(original));
		Collections.reverse(clone);
		return clone;
	}

	@Property
	boolean sizeRemainsTheSame(@ForAll List<Integer> original) {
		List<Integer> reversed = reverse(original);
		return original.size() == reversed.size();
	}

	@Property
	void allElementsStay(@ForAll List<Integer> original) {
		List<Integer> reversed = reverse(original);
		Assertions.assertThat(original).allMatch(element -> reversed.contains(element));
	}

	@Property
	boolean reverseMakesFirstElementLast(@ForAll List<Integer> original) {
		Assume.that(original.size() > 2);
		Integer lastReversed = reverse(original).get(original.size() - 1);
		return original.get(0).equals(lastReversed);
	}

	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> original) {
		return reverse(reverse(original)).equals(original);
	}

	@Property
	boolean reverseKeepsTheOriginalList(@ForAll List<Integer> aList) {
		return reverse(aList).equals(aList);
	}

	// Using wildcards and type variables in properties

	@Property
	//@Report(Reporting.GENERATED)
	boolean reverseWithWildcardType(@ForAll List<?> original) {
		return reverse(reverse(original)).equals(original);
	}

	@Property
	//@Report(Reporting.GENERATED)
	<T> boolean reverseWithTypeVariable(@ForAll List<T> original) {
		return reverse(reverse(original)).equals(original);
	}

}
