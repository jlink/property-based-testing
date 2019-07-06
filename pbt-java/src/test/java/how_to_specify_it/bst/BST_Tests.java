package how_to_specify_it.bst;

import java.util.*;
import java.util.AbstractMap.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class BST_Tests {

	private final BST<Integer, String> bst = BST.nil();

	@Example
	void new_bst_is_empty() {
		assertThat(bst.isEmpty()).isTrue();
	}

	@Example
	void an_inserted_value_can_be_found() {
		BST<Integer, String> updated = bst.insert(3, "three");
		assertThat(updated.isEmpty()).isFalse();
		assertThat(updated.find(3)).isPresent();
		assertThat(updated.find(3).get()).isEqualTo("three");

		assertThat(bst.isEmpty()).isTrue();
	}

	@Example
	void a_deleted_value_can_no_longer_be_found() {
		BST<Integer, String> updated1 = bst.insert(3, "three");
		BST<Integer, String> updated2 = updated1.delete(3);
		assertThat(updated2.find(3)).isNotPresent();
		assertThat(updated1.find(3)).isPresent();
	}

	@Example
	void keys_returns_set_of_inserted_keys() {
		BST<Integer, String> updated =
				bst.insert(1, "one")
				   .insert(2, "two")
				   .insert(3, "three");

		assertThat(updated.keys()).containsExactly(1, 2, 3);
	}

	@Example
	void toList_returns_key_value_pairs() {
		BST<Integer, String> updated =
				bst.insert(1, "one")
				   .insert(2, "two")
				   .insert(3, "three");

		assertThat(updated.toList()).containsExactly(
				new SimpleEntry<>(1, "one"),
				new SimpleEntry<>(2, "two"),
				new SimpleEntry<>(3, "three")
		);
	}

	@Example
	void union_of_two_bsts_contains_keys_of_both() {
		BST<Integer, String> one =
				bst.insert(1, "one")
				   .insert(2, "two")
				   .insert(3, "three");

		BST<Integer, String> two =
				bst.insert(4, "four")
				   .insert(5, "five")
				   .insert(3, "eerht");

		BST<Integer, String> union = BST.union(one, two);

		assertThat(union.toList()).containsExactly(
				new SimpleEntry<>(1, "one"),
				new SimpleEntry<>(2, "two"),
				new SimpleEntry<>(3, "three"),
				new SimpleEntry<>(4, "four"),
				new SimpleEntry<>(5, "five")
		);
	}
}
