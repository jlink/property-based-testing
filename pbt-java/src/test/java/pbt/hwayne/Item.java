package pbt.hwayne;

public class Item {
	public static Item withCost(int singleCost) {
		return new Item(singleCost);
	}

	private final int singleCost;

	public Item(int singleCost) {
		this.singleCost = singleCost;
	}

	public int singleCost() {
		return singleCost;
	}

	public boolean isForFree() {
		return singleCost == 0;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Item{");
		sb.append("singleCost=").append(singleCost);
		sb.append('}');
		return sb.toString();
	}
}
