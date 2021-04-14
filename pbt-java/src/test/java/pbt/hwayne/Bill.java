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

	public int totalCost() {
		return items.stream().mapToInt(Item::cost).sum();
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Bill{");
		sb.append("items=").append(items);
		sb.append('}');
		return sb.toString();
	}
}
