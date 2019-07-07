package how_to_specify_it.bst;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

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
		Arbitrary<Integer> keys = Arbitraries.integers();
		Arbitrary<Integer> values = Arbitraries.integers();
		Arbitrary<List<Tuple2<Integer, Integer>>> keysAndValues =
				Combinators.combine(keys, values).as(Tuple::of).list();

		// This could be implemented as streaming and reducing
		// but probably less understandable
		return keysAndValues.map(keyValueList -> {
			BST<Integer, Integer> bst = BST.nil();
			for (Tuple2<Integer, Integer> kv : keyValueList) {
				bst = bst.insert(kv.get1(), kv.get2());
			}
			return bst;
		});
	}
}

