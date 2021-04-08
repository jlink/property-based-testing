package pbt.hwayne;

import java.util.*;

public class Bill {
	public static Bill of(Item... items) {
		return new Bill(Arrays.asList(items));
	}

	private final List<Item> items;

	public Bill(List<Item> items) {
		this.items = items;
	}

	public List<Item> items() {
		return items;
	}

	public boolean isForFree() {
		return items.stream().allMatch(Item::isForFree);
	}
}
