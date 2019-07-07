package how_to_specify_it.bst;

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

	private BST(BST<K, V> left, Map.Entry<K, V> entry, BST<K, V> right) {

	}

	public boolean isEmpty() {
		return false;
	}

	//	find ::Ord k ⇒k →BST k v →Maybe v
	public Optional<V> find(K key) {
		return Optional.empty();
	}

	//	insert :: Ord k ⇒ k → v → BST k v → BST k v
	public BST<K, V> insert(K key, V value) {
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

	public int size() {
		return 0;
	}
}
