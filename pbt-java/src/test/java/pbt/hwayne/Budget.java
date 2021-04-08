package pbt.hwayne;

public class Budget {
	public static Budget withTotalLimit(int totalLimit) {
		return new Budget(totalLimit);
	}

	public Budget(int totalLimit) {

	}

	public boolean canAfford(Bill bill) {
		return false;
	}
}
