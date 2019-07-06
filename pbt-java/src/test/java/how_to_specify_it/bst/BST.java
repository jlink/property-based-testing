package how_to_specify_it.bst;

import java.util.*;

/**
 * The implementation wraps a JDK TreeMap so that the exposed interface is suitable to the paper
 * Moreover, every BST is cloned before changing it to allow the typical functional patterns.
 * This is by no means computational efficient.
 */
public class BST<K extends Comparable<K>, V> {

	//	nil :: BST k v
	public static <K extends Comparable<K>, V> BST<K, V> nil() {
		return new BST<>(new TreeMap<K, V>());
	}

	//	union :: Ord k ⇒ BST k v → BST k v → BST k v
	public static <K extends Comparable<K>, V> BST<K, V> union(BST<K, V> bst1, BST<K, V> bst2) {
		TreeMap<K, V> treeMap1 = bst1.treeMap;
		TreeMap<K, V> treeMap2 = bst2.cloneTreeMap();
		treeMap2.putAll(treeMap1);
		return new BST<>(treeMap2);
	}

	private final TreeMap<K, V> treeMap;

	private BST(TreeMap<K, V> treeMap) {
		this.treeMap = treeMap;
	}


	public boolean isEmpty() {
		return treeMap.isEmpty();
	}

	//	find ::Ord k ⇒k →BST k v →Maybe v
	public Optional<V> find(K key) {
		V value = treeMap.get(key);
		return value != null ? Optional.of(value) : Optional.empty();
	}

	//	insert :: Ord k ⇒ k → v → BST k v → BST k v
	public BST<K, V> insert(K key, V value) {
		TreeMap<K, V> treeMapToUpdate = cloneTreeMap();
		treeMapToUpdate.put(key, value);
		return new BST<>(treeMapToUpdate);
	}

	@SuppressWarnings("unchecked")
	private TreeMap<K, V> cloneTreeMap() {
		return (TreeMap<K, V>) treeMap.clone();
	}

	//	delete::Ord k ⇒k →BST k v →BST k v
	public BST<K, V> delete(K key) {
		if (!treeMap.containsKey(key))
			return this;
		TreeMap<K, V> treeMapToUpdate = cloneTreeMap();
		treeMapToUpdate.remove(key);
		return new BST<>(treeMapToUpdate);
	}

	//	keys ::BSTkv→[k]
	public Set<K> keys() {
		return treeMap.keySet();
	}

	//	toList :: BST k v → [ (k , v ) ]
	public List<Map.Entry<K, V>> toList() {
		return new ArrayList<>(treeMap.entrySet());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BST<?, ?> bst = (BST<?, ?>) o;

		return treeMap.equals(bst.treeMap);
	}

	@Override
	public int hashCode() {
		return treeMap.hashCode();
	}

	@Override
	public String toString() {
		return treeMap.toString();
	}
}
