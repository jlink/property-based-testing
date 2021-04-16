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
		Map<String, Integer> aggregatedTotals = aggregate(bill.items());
		for (Map.Entry<String, Integer> total : aggregatedTotals.entrySet()) {
			if (isOutsideCategoryBudget(total.getKey(), total.getValue())) {
				return false;
			}
		}
		return true;
	}

	private Map<String, Integer> aggregate(List<Item> items) {
		Map<String, Integer> aggregated = new HashMap<>();
		for (Item item : items) {
			if (!item.categories().isEmpty()) {
				// This is obviously a hack to remove later
				String category = item.categories().iterator().next();
				int total = aggregated.getOrDefault(category, 0);
				total += item.cost();
				aggregated.put(category, total);
			}
		}
		return aggregated;
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
