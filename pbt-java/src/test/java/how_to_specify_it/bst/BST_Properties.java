package how_to_specify_it.bst;

import java.util.*;

import net.jqwik.api.*;

import static how_to_specify_it.bst.BSTValidity.*;

class BST_Properties {

	@Property
	boolean arbitrary_valid(@ForAll("trees") BST<Integer, Integer> bst) {
		return isValid(bst);
	}

	@Example
	boolean nil_valid() {
		BST<?, ?> nil = BST.nil();
		return isValid(nil);
	}

	@Property
	boolean insert_valid(
			@ForAll("trees") BST<Integer, Integer> bst,
			@ForAll Integer key
	) {
		return isValid(bst.insert(key, 42));
	}

	@Property
	boolean delete_valid(
			@ForAll("trees") BST<Integer, Integer> bst,
			@ForAll Integer key
	) {
		// Assume.that(isValid(bst));
		return isValid(bst.delete(key));
	}

	@Property
	boolean union_valid(
			@ForAll("trees") BST<Integer, Integer> bst,
			@ForAll("trees") BST<Integer, Integer> other
	) {
		return isValid(BST.union(bst, other));
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
