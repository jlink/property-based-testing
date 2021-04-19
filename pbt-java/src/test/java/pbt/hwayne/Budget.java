package pbt.hwayne;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

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

		Set<String> limitedCategories = limits.stream().map(Limit::category).collect(Collectors.toSet());
		Predicate<Item> itemIsTrivial = i -> i.categories().size() <= 1
												 || i.categories().stream().noneMatch(limitedCategories::contains);
		List<Item> trivialItems = filterItems(bill, itemIsTrivial);
		if (itemsDoNotFitInBudget(availableBudgets, trivialItems)) return false;

		List<Item> nonTrivialItems = filterItems(bill, itemIsTrivial.negate());
		Set<List<Item>> permutations = permutations(nonTrivialItems);
		return permutations.stream().anyMatch(items -> {
			Map<String, Integer> available = new HashMap<>(availableBudgets);
			return !itemsDoNotFitInBudget(available, items);
		});
	}

	private Set<List<Item>> permutations(List<Item> items) {
		// Using jqwik to calculate permutations because it's easy. In a real app I'd probably run a local, optimized implementation.
		return Arbitraries.shuffle(items).allValues().map(s -> s.collect(Collectors.toSet())).orElse(Collections.emptySet());
	}

	private List<Item> filterItems(Bill bill, Predicate<Item> itemPredicate) {
		return bill.items().stream().filter(itemPredicate).collect(Collectors.toList());
	}

	private boolean itemsDoNotFitInBudget(Map<String, Integer> availableBudgets, List<Item> items) {
		for (Item item : items) {
			if (noBudgetApplies(item)) {
				continue;
			}
			Optional<String> fittingCategory = findCategoryWithFittingBudget(item, availableBudgets);
			if (!fittingCategory.isPresent()) {
				return true;
			}
			fittingCategory.ifPresent(category -> updateBudgets(availableBudgets, category, item.cost()));
		}
		return false;
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
