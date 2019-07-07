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
		if (entry.getKey().compareTo(key) == 0) {
			return Optional.of(entry.getValue());
		}
		if (entry.getKey().compareTo(key) > 0) {
			return getLeft().find(key);
		}
		if (entry.getKey().compareTo(key) < 0) {
			return getRight().find(key);
		}
		throw new RuntimeException("Should never get here");
	}

	//	insert :: Ord k ⇒ k → v → BST k v → BST k v
	public BST<K, V> insert(K key, V value) {
		if (entry == null) {
			return new BST<>(left, new SimpleImmutableEntry<>(key, value), right);
		}
		if (entry.getKey().compareTo(key) == 0) {
			return new BST<>(left, new SimpleImmutableEntry<>(key, value), right);
		}
		if (entry.getKey().compareTo(key) > 0) {
			return new BST<>(getLeft().insert(key, value), entry, right);
		}
		if (entry.getKey().compareTo(key) < 0) {
			return new BST<>(left, entry, getRight().insert(key, value));
		}
		return this;
	}

	private BST<K, V> getRight() {
		return this.right == null ? NIL : this.right;
	}

	private BST<K, V> getLeft() {
		return this.left == null ? NIL : this.left;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BST<?, ?> bst = (BST<?, ?>) o;
		if (!Objects.equals(left, bst.left)) return false;
		if (!Objects.equals(entry, bst.entry)) return false;
		return Objects.equals(right, bst.right);
	}

	@Override
	public int hashCode() {
		int result = left != null ? left.hashCode() : 0;
		result = 31 * result + (entry != null ? entry.hashCode() : 0);
		result = 31 * result + (right != null ? right.hashCode() : 0);
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
		String leftString =
				left == null ? "NIL" : left.toIndentedString(indent + 1);
		String rightString =
				right == null ? "NIL" : right.toIndentedString(indent + 1);
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
