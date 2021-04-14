package pbt.hwayne;

public class Item {
	public static Item withCost(int singleCost) {
		return withCostAndCount(singleCost, 1);
	}

	public static Item withCostAndCount(int singleCost, int count) {
		return new Item(singleCost, count);
	}

	private final int singleCost;
	private final int count;

	public Item(int singleCost, int count) {
		this.singleCost = singleCost;
		this.count = count;
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
		sb.append('}');
		return sb.toString();
	}

	public int cost() {
		return singleCost * count;
	}
}
