package pbt.demos;

import java.util.*;

import net.jqwik.api.*;

class ListReverseProperties {

	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> original) {
		return reverse(reverse(original)).equals(original);
	}

	private <T> List<T> reverse(List<T> original) {
		List<T> clone = new ArrayList<>(original);
		Collections.reverse(clone);
		return clone;
	}

	@Property
	boolean reverseMakesFirstElementLast(@ForAll List<Integer> original) {
		Assume.that(original.size() > 2);
		Integer lastReversed = reverse(original).get(original.size() - 1);
		return original.get(0).equals(lastReversed);
	}

	@Property
	boolean sizeRemainsTheSame(@ForAll List<Integer> original) {
		List<Integer> reversed = reverse(original);
		return original.size() == reversed.size();
	}


	// Using wildcards and type variables in properties

	@Property(reporting = Reporting.GENERATED)
	boolean reverseWithWildcardType(@ForAll List<?> original) {
		return reverse(reverse(original)).equals(original);
	}

	@Property(reporting = Reporting.GENERATED)
	<T> boolean reverseWithTypeVariable(@ForAll List<T> original) {
		return reverse(reverse(original)).equals(original);
	}

}
