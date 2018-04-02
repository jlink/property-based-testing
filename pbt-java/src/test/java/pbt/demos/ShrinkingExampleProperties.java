package pbt.demos;

import java.util.*;

import net.jqwik.api.*;

import static java.util.Collections.*;

class ShrinkingExampleProperties {

	static <E> List<E> brokenReverse(List<E> aList) {
		if (aList.size() < 4) {
			aList = new ArrayList<>(aList);
			reverse(aList);
		}
		return aList;
	}

	@Property(shrinking = ShrinkingMode.FULL)
	boolean reverseShouldSwapFirstAndLast(@ForAll List<Integer> aList) {
		Assume.that(!aList.isEmpty());
		List<Integer> reversed = brokenReverse(aList);
		return aList.get(0) == reversed.get(aList.size() - 1);
	}
}
