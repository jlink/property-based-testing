package how_to_specify_it.bst;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

import static how_to_specify_it.bst.BSTUtils.*;

class BST_Properties {

	@Property
	boolean arbitrary_valid(@ForAll("trees") BST<Integer, Integer> bst) {
		return isValid(bst);
	}

	@Group
	class Validity {
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
	}

	@Group
	class Postconditions {

		// prop_InsertPost k v t k′ =
		//   find k′ (insert k v t) === if k ≡ k′ then Just v else find k′ t
		@Property
		boolean insert_post(
				@ForAll Integer key, @ForAll Integer value,
				@ForAll("trees") BST<Integer, Integer> bst,
				@ForAll Integer otherKey
		) {
			// Statistics.collect(key.equals(otherKey));

			Optional<Integer> found = bst.insert(key, value).find(otherKey);
			if (otherKey.equals(key)) {
				return found.map(v -> v.equals(value)).orElse(false);
			} else {
				return found.equals(bst.find(otherKey));
			}
		}

		@Property
		boolean insert_post_same_key(
				@ForAll Integer key, @ForAll Integer value,
				@ForAll("trees") BST<Integer, Integer> bst
		) {
			return insert_post(key, value, bst, key);
		}

		// prop_UnionPost t t′ k = find k (union t t′) === (find k t <|> find k t′)
		@Property
		boolean union_post(
				@ForAll("trees") BST<Integer, Integer> left,
				@ForAll("trees") BST<Integer, Integer> right,
				@ForAll Integer key
		) {
			// boolean keyInLeft = left.find(key).isPresent();
			// boolean keyInRight = right.find(key).isPresent();
			// Statistics.collect(keyInLeft, keyInRight);

			BST<Integer, Integer> union = BST.union(left, right);
			Integer previousValue = left.find(key).orElse(right.find(key).orElse(null));
			Integer unionValue = union.find(key).orElse(null);
			return Objects.equals(unionValue, previousValue);
		}

		@Property
		boolean find_post_present(
				@ForAll Integer key, @ForAll Integer value,
				@ForAll("trees") BST<Integer, Integer> bst
		) {
			return bst.insert(key, value).find(key).equals(Optional.of(value));
		}

		@Property
		boolean find_post_absent(
				@ForAll Integer key,
				@ForAll("trees") BST<Integer, Integer> bst
		) {
			return bst.delete(key).find(key).equals(Optional.empty());
		}

		// prop_InsertDeleteComplete k t = case find k t of
		//   Nothing → t === delete k t
		//   Just v →t ===insert k v t
		@Property
		boolean insert_delete_complete(
				@ForAll Integer key,
				@ForAll("trees") BST<Integer, Integer> bst
		) {
			Optional<Integer> found = bst.find(key);
			if (!found.isPresent()) {
				return bst.equals(bst.delete(key));
			} else {
				return bst.equals(bst.insert(key, found.get()));
			}
		}
	}

	@Group
	class Metamorphic {

		//prop InsertInsert (k, v) (k′, v′) t =
		//  insert k v (insert k′ v′ t) === insert k′ v′ (insert k v t)
		@Property
		@Disabled
		boolean insert_insert1(
				@ForAll Integer key1, @ForAll Integer value1,
				@ForAll Integer key2, @ForAll Integer value2,
				@ForAll("trees") BST<Integer, Integer> bst
		) {
			return bst.insert(key1, value1).insert(key2, value2)
					  .equals(bst.insert(key2, value2).insert(key1, value1));
		}

		// prop InsertInsert (k, v) (k′, v′) t = insert k v (insert k′ v′ t)
		//   ===
		//   if k ≡ k′ then insert k v t else insert k′ v′ (insert k v t)
		@Property
		boolean insert_insert(
				@ForAll Integer key1, @ForAll Integer value1,
				@ForAll Integer key2, @ForAll Integer value2,
				@ForAll("trees") BST<Integer, Integer> bst
		) {
			BST<Integer, Integer> inserted = bst.insert(key1, value1).insert(key2, value2);
			BST<Integer, Integer> expected =
					key1.equals(key2)
							? bst.insert(key2, value2)
							: bst.insert(key2, value2).insert(key1, value1);
			return equivalent(inserted, expected);
		}

		@Property
		boolean insert_insert_weak(
				@ForAll Integer key1, @ForAll Integer value1,
				@ForAll Integer key2, @ForAll Integer value2,
				@ForAll("trees") BST<Integer, Integer> bst
		) {
			Assume.that(!key1.equals(key2));
			return equivalent(
					bst.insert(key1, value1).insert(key2, value2),
					bst.insert(key2, value2).insert(key1, value1)
			);
		}

		// prop InsertDelete (k,v) k′ t =
		//   insert k v (delete k′ t)
		//   􏰂equivalent
		//   if k ≡ k′ then insert k v t else delete k′ (insert k v t)
		@Property
		boolean insert_delete(
				@ForAll Integer key1,
				@ForAll Integer key2, @ForAll Integer value2,
				@ForAll("trees") BST<Integer, Integer> bst
		) {
			BST<Integer, Integer> deleted = bst.delete(key1).insert(key2, value2);
			BST<Integer, Integer> expected =
					key1.equals(key2)
							? bst.insert(key2, value2)
							: bst.insert(key2, value2).delete(key1);
			return equivalent(deleted, expected);
		}

		// prop InsertUnion (k, v) t t′ =
		//   insert k v (union t t′) 􏰂
		//   equivalent
		//   union (insert k v t) t′
		@Property
		boolean insert_union(
				@ForAll Integer key, @ForAll Integer value,
				@ForAll("trees") BST<Integer, Integer> bst1,
				@ForAll("trees") BST<Integer, Integer> bst2
		) {
			BST<Integer, Integer> unionInsert = BST.union(bst1, bst2).insert(key, value);
			BST<Integer, Integer> insertUnion = BST.union(bst1.insert(key, value), bst2);
			return equivalent(unionInsert, insertUnion);
		}
	}

	@Group
	class Equivalence {

		// prop InsertPreservesEquiv k v t t′ =
		//   t _equivalent_ t′ =⇒ insert k v t _equivalent_ insert k v t′
		@Property
		@Disabled("Does not generate enough examples with assumption fulfilled")
		boolean insert_preserves_equivalence_exhausted(
				@ForAll Integer key, @ForAll Integer value,
				@ForAll("trees") BST<Integer, Integer> bst1,
				@ForAll("trees") BST<Integer, Integer> bst2
		) {
			Assume.that(equivalent(bst1, bst2));
			return equivalent(
					bst1.insert(key, value),
					bst2.insert(key, value)
			);
		}

		// prop InsertPreservesEquiv k v (t :􏰂: t′) = insert k v t 􏰂 insert k v t′
		@Property
		boolean insert_preserves_equivalence(
				@ForAll Integer key, @ForAll Integer value,
				@ForAll("equivalentTrees") Tuple2<BST<Integer, Integer>, BST<Integer, Integer>> bsts
		) {
			return equivalent(
					bsts.get1().insert(key, value),
					bsts.get2().insert(key, value)
			);
		}

		// prop DeletePreservesEquiv k (t :􏰂: t′) = delete k t 􏰂 delete k t′
		@Property
		boolean delete_preserves_equivalence(
				@ForAll Integer key,
				@ForAll("equivalentTrees") Tuple2<BST<Integer, Integer>, BST<Integer, Integer>> bsts
		) {
			return equivalent(
					bsts.get1().delete(key),
					bsts.get2().delete(key)
			);
		}

		// prop UnionPreservesEquiv (t1 :􏰂: t1′) (t2 :􏰂: t2′) = union t1 t2 􏰂 union t1′ t2′
		@Property
		boolean union_preserves_equivalence(
				@ForAll("equivalentTrees") Tuple2<BST<Integer, Integer>, BST<Integer, Integer>> bsts1,
				@ForAll("equivalentTrees") Tuple2<BST<Integer, Integer>, BST<Integer, Integer>> bsts2
		) {
			return equivalent(
					BST.union(bsts1.get1(), bsts2.get1()),
					BST.union(bsts1.get2(), bsts2.get2())
			);
		}

		// prop FindPreservesEquiv k (t :􏰂: t′) = find k t === find k t′
		@Property
		boolean find_preserves_equivalence(
				@ForAll Integer key,
				@ForAll("equivalentTrees") Tuple2<BST<Integer, Integer>, BST<Integer, Integer>> bsts
		) {
			return bsts.get1().find(key).equals(bsts.get2().find(key));
		}

		// prop Equivs (t :􏰂:t′)=t 􏰂t′
		@Property
		boolean equivalent_trees_are_equivalent(
				@ForAll("equivalentTrees") Tuple2<BST<Integer, Integer>, BST<Integer, Integer>> bsts
		) {
			return equivalent(bsts.get1(), bsts.get2());
		}

		@Provide
		Arbitrary<Tuple2<BST, BST>> equivalentTrees() {
			Arbitrary<Integer> keys = Arbitraries.integers().unique();
			Arbitrary<Integer> values = Arbitraries.integers();
			Arbitrary<List<Tuple2<Integer, Integer>>> keysAndValues =
					Combinators.combine(keys, values).as(Tuple::of).list();

			return keysAndValues.map(keyValueList -> {
				BST<Integer, Integer> bst1 = BST.nil();
				for (Tuple2<Integer, Integer> kv : keyValueList) {
					bst1 = bst1.insert(kv.get1(), kv.get2());
				}
				Collections.shuffle(keyValueList);
				BST<Integer, Integer> bst2 = BST.nil();
				for (Tuple2<Integer, Integer> kv : keyValueList) {
					bst2 = bst2.insert(kv.get1(), kv.get2());
				}
				return Tuple.of(bst1, bst2);
			});
		}
	}

	@Provide
	Arbitrary<BST<Integer, Integer>> trees() {
		Arbitrary<Integer> keys = Arbitraries.integers().unique();
		Arbitrary<Integer> values = Arbitraries.integers();
		Arbitrary<List<Tuple2<Integer, Integer>>> keysAndValues =
				Combinators.combine(keys, values).as(Tuple::of).list();

		// This could be implemented as streaming and reducing
		// but that'd probably be less understandable
		return keysAndValues.map(keyValueList -> {
			BST<Integer, Integer> bst = BST.nil();
			for (Tuple2<Integer, Integer> kv : keyValueList) {
				bst = bst.insert(kv.get1(), kv.get2());
			}
			return bst;
		});
	}

}

