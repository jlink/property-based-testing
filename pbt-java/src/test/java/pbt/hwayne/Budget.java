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
		Map<String, Integer> availableBudgets = initialBudgets(limits);
		for (Item item : bill.items()) {
			if (noBudgetApplies(item)) {
				continue;
			}
			Optional<String> fittingCategory = findCategoryWithFittingBudget(item, availableBudgets);
			if (!fittingCategory.isPresent()) {
				return false;
			}
			fittingCategory.ifPresent(category -> updateBudgets(availableBudgets, category, item.cost()));
		}
		return true;
	}

	private boolean noBudgetApplies(Item item) {
		return limits.stream().map(Limit::category).noneMatch(limit -> item.categories().contains(limit));
	}

	private void updateBudgets(Map<String, Integer> availableBudgets, String category, int cost) {
		int budgetBefore = availableBudgets.get(category);
		availableBudgets.put(category, budgetBefore - cost);
	}

	private Optional<String> findCategoryWithFittingBudget(Item item, Map<String, Integer> budgets) {
		return budgets.entrySet()
					  .stream()
					  .filter(entry -> item.categories().contains(entry.getKey()))
					  .filter(entry -> entry.getValue() >= item.cost())
					  .map(Map.Entry::getKey)
					  .findFirst();
	}

	private Map<String, Integer> initialBudgets(Set<Limit> limits) {
		Map<String, Integer> budgets = new HashMap<>();
		for (Limit limit : limits) {
			budgets.put(limit.category(), limit.amount());
		}
		return budgets;
	}

	private boolean isOutsideTotalBudget(Bill bill) {
		return bill.totalCost() > totalLimit;
	}
}
