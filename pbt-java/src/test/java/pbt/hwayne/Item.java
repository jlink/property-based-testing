package pbt.hwayne;

import java.util.*;

public class Item {
	public static final int MAX_SINGLE_COST = 1000;
	public static final int DEFAULT_COUNT = 1;
	public static final int MAX_COUNT = 100;

	public static Item withCost(int singleCost) {
		return with(singleCost, DEFAULT_COUNT);
	}

	public static Item with(int singleCost, int count, String ... categories) {
		return new Item(singleCost, count, categories);
	}

	private final int singleCost;
	private final int count;
	private final Set<String> categories;

	private Item(int singleCost, int count, String[] categories) {
		this.singleCost = singleCost;
		this.count = count;
		this.categories = new HashSet<>(Arrays.asList(categories));
	}

	public int singleCost() {
		return singleCost;
	}

	public int count() {
		return count;
	}

	public boolean isForFree() {
		return singleCost == 0;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Item{");
		sb.append("singleCost=").append(singleCost);
		sb.append(", count=").append(count);
		sb.append(", categories=").append(categories);
		sb.append('}');
		return sb.toString();
	}

	public int cost() {
		return singleCost * count;
	}

	public Set<String> categories() {
		return categories;
	}
}
