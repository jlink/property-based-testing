package how_to_specify_it.bst;

import java.util.*;
import java.util.stream.*;

class BSTValidity {

	public static <K extends Comparable<K>, V> boolean isValid(BST<K, V> bst) {
		if (bst.isLeaf()) {
			return true;
		}
		return isValid(bst.left()) && isValid(bst.right())
					   && keys(bst.left()).allMatch(k -> k.compareTo(bst.key()) < 0)
					   && keys(bst.right()).allMatch(k -> k.compareTo(bst.key()) > 0);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static <K extends Comparable<K>, V> Stream<K> keys(Optional<BST<K, V>> bst) {
		return bst.map(BST::keys).orElse(Collections.emptyList()).stream();
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static <K extends Comparable<K>, V> boolean isValid(Optional<BST<K, V>> optionalBST) {
		return optionalBST.map(BSTValidity::isValid).orElse(true);
	}

}
