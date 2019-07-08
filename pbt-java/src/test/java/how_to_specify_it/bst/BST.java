package how_to_specify_it.bst;

import java.util.AbstractMap.*;
import java.util.*;

public class BST<K extends Comparable<K>, V> {

	private static final BST NIL = new BST<>();

	//	nil :: BST k v
	public static <K extends Comparable<K>, V> BST<K, V> nil() {
		//noinspection unchecked
		return BST.NIL;
	}

	//	union :: Ord k ⇒ BST k v → BST k v → BST k v
	public static <K extends Comparable<K>, V> BST<K, V> union(BST<K, V> bst1, BST<K, V> bst2) {
		BST<K, V> union = bst2;
		for (Map.Entry<K, V> entry : bst1.toList()) {
			union = union.insert(entry);
		}
		return union;
	}

	private final BST<K, V> left;
	private final Map.Entry<K, V> entry;
	private final BST<K, V> right;

	private BST() {
		this(nil(), null, nil());
	}

	private BST(BST<K, V> left, Map.Entry<K, V> entry, BST<K, V> right) {
		this.left = left;
		this.entry = entry;
		this.right = right;
	}

	public K key() {
		return entry == null ? null : entry.getKey();
	}

	public V value() {
		return entry == null ? null : entry.getValue();
	}

	public Optional<BST<K, V>> left() {
		if (getLeft() == NIL)
			return Optional.empty();
		return Optional.of(left);
	}

	public Optional<BST<K, V>> right() {
		if (getRight() == NIL)
			return Optional.empty();
		return Optional.of(right);
	}

	public boolean isLeaf() {
		return getLeft() == NIL && getRight() == NIL;
	}

	public boolean isEmpty() {
		return entry == null;
	}

	public int size() {
		if (entry == null) {
			return 0;
		}
		return 1 + getLeft().size() + getRight().size();
	}

	//	find ::Ord k ⇒k →BST k v →Maybe v
	public Optional<V> find(K key) {
		if (entry == null) {
			return Optional.empty();
		}
		if (entry.getKey().compareTo(key) > 0) {
			return getLeft().find(key);
		}
		if (entry.getKey().compareTo(key) < 0) {
			return getRight().find(key);
		}
		return Optional.of(entry.getValue());
	}

	//	insert :: Ord k ⇒ k → v → BST k v → BST k v
	public BST<K, V> insert(K key, V value) {
		SimpleImmutableEntry<K, V> newEntry = new SimpleImmutableEntry<>(key, value);
		return insert(newEntry);
	}

	private BST<K, V> insert(Map.Entry<K, V> newEntry) {
		if (this.entry == null) {
			return new BST<>(left, newEntry, right);
		}
		if (this.entry.getKey().compareTo(newEntry.getKey()) > 0) {
			return new BST<>(getLeft().insert(newEntry), this.entry, right);
		}
		if (this.entry.getKey().compareTo(newEntry.getKey()) < 0) {
			return new BST<>(left, this.entry, getRight().insert(newEntry));
		}
		// bug(2):
		// return new BST<>(left, this.entry, getRight().insert(newEntry));
		return new BST<>(left, newEntry, right);
	}

	private BST<K, V> getRight() {
		return this.right == null ? NIL : this.right;
	}

	private BST<K, V> getLeft() {
		return this.left == null ? NIL : this.left;
	}

	//	delete::Ord k ⇒k →BST k v →BST k v
	public BST<K, V> delete(K key) {
		if (entry == null) {
			return this;
		}
		if (entry.getKey().compareTo(key) > 0) {
			return new BST<>(getLeft().delete(key), entry, right);
		}
		if (entry.getKey().compareTo(key) < 0) {
			return new BST<>(left, entry, getRight().delete(key));
		}
		if (isLeaf()) {
			return NIL;
		} else {
			if (getLeft() == NIL) {
				return right;
			}
			if (getRight() == NIL) {
				return left;
			}
			return right.insert(left.entry);
		}
	}

	//	keys ::BSTkv→[k]
	public List<K> keys() {
		if (entry == null) {
			return Collections.emptyList();
		}
		List<K> keys = new ArrayList<>();
		keys.add(entry.getKey());
		keys.addAll(getLeft().keys());
		keys.addAll(getRight().keys());
		return keys;
	}

	//	toList :: BST k v → [ (k , v ) ]
	public List<Map.Entry<K, V>> toList() {
		if (entry == null) {
			return Collections.emptyList();
		}
		List<Map.Entry<K, V>> entries = new ArrayList<>();
		entries.add(entry);
		entries.addAll(getLeft().toList());
		entries.addAll(getRight().toList());
		return entries;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BST<?, ?> bst = (BST<?, ?>) o;
		if (!Objects.equals(getLeft(), bst.getLeft())) return false;
		if (!Objects.equals(entry, bst.entry)) return false;
		return Objects.equals(getRight(), bst.getRight());
	}

	@Override
	public int hashCode() {
		if (entry == null) {
			return 0;
		}
		int result = getLeft().hashCode();
		result = 31 * result + entry.hashCode();
		result = 31 * result + getRight().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return toIndentedString(0);
	}

	private String toIndentedString(int indent) {
		if (entry == null) {
			return "NIL";
		}
		String leftString = getLeft().toIndentedString(indent + 1);
		String rightString = getRight().toIndentedString(indent + 1);
		String indentation = String.join("", Collections.nCopies(indent, "       "));
		return String.format(
				"%s%n%sleft:  %s%n%sright: %s",
				entry.toString(),
				indentation,
				leftString,
				indentation,
				rightString
		);
	}
}
