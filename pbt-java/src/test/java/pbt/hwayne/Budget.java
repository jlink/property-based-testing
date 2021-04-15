package pbt.hwayne;

import java.util.*;

public class Budget {
	public static Budget withTotalLimit(int totalLimit) {
		return with(totalLimit, Collections.emptySet());
	}

	public static Budget with(int totalLimit, Set<Limit> limits) {
		return new Budget(totalLimit, limits);
	}

	private final int totalLimit;
	private final Set<Limit> limits;

	private Budget(int totalLimit, Set<Limit> limits) {
		this.totalLimit = totalLimit;
		this.limits = limits;
	}

	public int totalLimit() {
		return totalLimit;
	}

	public Set<Limit> limits() {
		return limits;
	}

	public boolean canAfford(Bill bill) {
		if (isOutsideTotalBudget(bill)) {
			return false;
		}
		for (Item item : bill.items()) {
			if (item.category().isPresent()) {
				if (isOutsideCategoryBudget(item.category().get(), item.cost())) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isOutsideCategoryBudget(String category, int cost) {
		return limits.stream()
					 .filter(limit -> limit.category().equals(category))
					 .anyMatch(limit -> limit.amount() < cost);
	}

	private boolean isOutsideTotalBudget(Bill bill) {
		return bill.totalCost() > totalLimit;
	}
}
