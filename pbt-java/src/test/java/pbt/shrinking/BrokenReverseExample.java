package pbt.shrinking;

import java.util.*;

import net.jqwik.api.*;

import static pbt.shrinking.BrokenReverse.*;

class BrokenReverseExample {

	@Property(shrinking = ShrinkingMode.OFF)
	boolean reverseSwapsFirstAndLast(@ForAll List<Integer> aList) {
		Assume.that(!aList.isEmpty());
		List<Integer> reversed = brokenReverse(aList);
		Integer lastReversed = reversed.get(aList.size() - 1);
		return aList.get(0).equals(lastReversed);
	}

}
