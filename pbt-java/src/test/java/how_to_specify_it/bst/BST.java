package how_to_specify_it.bst;

import java.util.AbstractMap.*;
import java.util.*;

public class BST<K extends Comparable<K>, V> {

	private static final BST NIL = new BST<>(null, null, null);

	//	nil :: BST k v
	public static <K extends Comparable<K>, V> BST<K, V> nil() {
		//noinspection unchecked
		return BST.NIL;
	}

	//	union :: Ord k ⇒ BST k v → BST k v → BST k v
	public static <K extends Comparable<K>, V> BST<K, V> union(BST<K, V> bst1, BST<K, V> bst2) {
		return null;
	}

	private final BST<K, V> left;
	private final Map.Entry<K, V> entry;
	private final BST<K, V> right;

	private BST(BST<K, V> left, Map.Entry<K, V> entry, BST<K, V> right) {
		this.left = left;
		this.entry = entry;
		this.right = right;
	}

	public Optional<BST<K, V>> left() {
		return Optional.of(left);
	}

	public Optional<BST<K, V>> right() {
		return Optional.of(right);
	}

	public boolean isEmpty() {
		return entry == null;
	}

	public int size() {
		if (entry != null) {
			return 1;
		}
		return 0;
	}

	//	find ::Ord k ⇒k →BST k v →Maybe v
	public Optional<V> find(K key) {
		if (entry == null) {
			return Optional.empty();
		}
		if (entry.getKey().compareTo(key) == 0) {
			return Optional.of(entry.getValue());
		}
		return Optional.empty();
	}

	//	insert :: Ord k ⇒ k → v → BST k v → BST k v
	public BST<K, V> insert(K key, V value) {
		if (entry == null) {
			return new BST<>(left, new SimpleImmutableEntry<>(key, value), right);
		}
		return this;
	}

	//	delete::Ord k ⇒k →BST k v →BST k v
	public BST<K, V> delete(K key) {
		return this;
	}

	//	keys ::BSTkv→[k]
	public List<K> keys() {
		return Collections.emptyList();
	}

	//	toList :: BST k v → [ (k , v ) ]
	public List<Map.Entry<K, V>> toList() {
		return Collections.emptyList();
	}

}
