package pbt.reverse;

import static java.util.Collections.*;

import java.util.*;

import net.jqwik.api.*;

class ReverseProperties {

	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> aList) {
		List<Integer> copy = new ArrayList<>(aList);
		reverse(copy);
		reverse(copy);
		return copy.equals(aList);
	}
}
