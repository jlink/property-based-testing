package pbt.hwayne;

public class Budget {
	public static Budget withTotalLimit(int totalLimit) {
		return new Budget(totalLimit);
	}

	private final int totalLimit;

	public Budget(int totalLimit) {
		this.totalLimit = totalLimit;
	}

	public boolean canAfford(Bill bill) {
		return bill.totalCost() <= totalLimit;
	}
}
