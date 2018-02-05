package pbt.demos;

import net.jqwik.api.*;

import java.util.*;

class SortingProperties {

	@Property(reporting = Reporting.GENERATED)
	boolean sortingAListWorks(@ForAll List<Integer> unsorted) {
		return isSorted(sort(unsorted));
	}

	private boolean isSorted(List<Integer> sorted) {
		if (sorted.size() <= 1) return true;
		return sorted.get(0) <= sorted.get(1) //
				&& isSorted(sorted.subList(1, sorted.size()));
	}

	private List<Integer> sort(List<Integer> unsorted) {
		unsorted.sort((a, b) -> a > b ? 1 : -1);
		return unsorted;
	}
}
