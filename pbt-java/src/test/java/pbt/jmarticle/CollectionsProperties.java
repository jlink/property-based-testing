package pbt.jmarticle;

import java.util.*;

import net.jqwik.api.*;

class CollectionsProperties {

	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> aList) {
		List<Integer> copy = new ArrayList<>(aList);
		Collections.reverse(copy);
		Collections.reverse(copy);
		return copy.equals(aList);
	}

}
