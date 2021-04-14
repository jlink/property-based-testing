package pbt.hwayne;

import java.util.*;

public class Item {
	public static final int MAX_SINGLE_COST = 1000;
	public static final int DEFAULT_COUNT = 1;
	public static final int MAX_COUNT = 100;

	public static Item withCost(int singleCost) {
		return withCostAndCount(singleCost, DEFAULT_COUNT);
	}

	public static Item withCostAndCount(int singleCost, int count) {
		return new Item(singleCost, count, null);
	}

	public static Item with(int singleCost, int count, String category) {
		return new Item(singleCost, count, category);
	}

	private final int singleCost;
	private final int count;
	private final String category;

	public Item(int singleCost, int count, String category) {
		this.singleCost = singleCost;
		this.count = count;
		this.category = category;
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
		sb.append(", category='").append(category).append('\'');
		sb.append('}');
		return sb.toString();
	}

	public int cost() {
		return singleCost * count;
	}

	public Optional<String> category() {
		return Optional.ofNullable(category);
	}
}
