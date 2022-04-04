package pbt.demos;

import java.util.*;

import net.jqwik.api.*;

class SortingProperties {

	@Property
	boolean sortingAListWorks(@ForAll List<Integer> unsorted) {
		return isSorted(sortAscending(unsorted));
	}

	private boolean isSorted(List<Integer> sorted) {
		// if (sorted.size() <= 1) return true;
		return sorted.get(0) <= sorted.get(1)
				   && isSorted(sorted.subList(1, sorted.size()));
	}

	private List<Integer> sortAscending(List<Integer> unsorted) {
		unsorted.sort((a, b) -> a > b ? 1 : -1);
		return unsorted;
	}
}
