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
