package pbt.shrinking;

import java.util.*;

import static java.util.Collections.*;

class BrokenReverse {

	static <E> List<E> brokenReverse(List<E> aList) {
		if (aList.size() < 4) {
			aList = new ArrayList<>(aList);
			reverse(aList);
		}
		return aList;
	}


}
