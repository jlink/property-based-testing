package pbt.jmarticle;

import net.jqwik.api.*;

import java.util.*;

class CollectionsProperties {

	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> aList) {
		List<Integer> copy = new ArrayList<>(aList);
		Collections.reverse(copy);
		Collections.reverse(copy);
		return copy.equals(aList);
	}

	@Property(reporting = Reporting.GENERATED)
	boolean joiningTwoLists( //
			@ForAll List<String> list1, //
			@ForAll List<String> list2
	) {
		List<String> joinedList = new ArrayList<>(list1);
		joinedList.addAll(list2);
		return joinedList.size() == list1.size() + list2.size();
	}

}
