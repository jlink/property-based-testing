package how_to_specify_it.bst;

import java.util.*;

import net.jqwik.api.*;

class BST_Properties {

	@Property(tries = 1000)
	boolean checkGenerator(@ForAll("trees") BST<Integer, Integer> bst) {
		String criterion =
				bst.size() == 0 ? "empty" :
						bst.size() <= 10 ? "<= 10" :
								bst.size() <= 100 ? "<= 100" :
										bst.size() <= 1000 ? "<= 1000" : "> 1000";
		Statistics.collect(criterion);
		//System.out.println(bst.size());
		//System.out.println(bst);

		return BSTValidity.isValid(bst);
	}

	@Provide
	Arbitrary<BST<Integer, Integer>> trees() {
		Arbitrary<List<Integer>> keys = Arbitraries.integers().list();
		return keys.flatMap(keyList -> {
			Arbitrary<List<Integer>> values = Arbitraries.integers().list().ofSize(keyList.size());
			return values.map(valuesList -> {
				BST<Integer, Integer> bst = BST.nil();
				for (int i = 0; i < keyList.size(); i++) {
					Integer key = keyList.get(i);
					Integer value = valuesList.get(i);
					bst = bst.insert(key, value);
				}
				return bst;
			});
		});
	}

}
