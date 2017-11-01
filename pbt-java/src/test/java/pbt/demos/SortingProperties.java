package pbt.demos;

import java.util.List;

import net.jqwik.api.*;

class SortingProperties {

	@Property(reporting = ReportingMode.GENERATED)
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
