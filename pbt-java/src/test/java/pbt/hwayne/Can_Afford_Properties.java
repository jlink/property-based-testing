package pbt.hwayne;

import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;

import static org.assertj.core.api.Assertions.*;

class Can_Afford_Properties {

	@Property
	void cannot_afford_zero_budget(@ForAll("bills") Bill billWithItems) {
		Assume.that(!billWithItems.isForFree());
		Budget zeroBudget = Budget.withTotalLimit(0);
		assertThat(zeroBudget.canAfford(billWithItems)).isFalse();
	}

	@Property
	void total_limit_of_budget_used_for_afford(
		@ForAll @IntRange(min = 1) int totalLimit,
		@ForAll("bills") Bill bill
	) {
		Budget budget = Budget.withTotalLimit(totalLimit);

		boolean canBeAfforded = bill.totalCost() <= totalLimit;
		Statistics.label("bill can be afforded")
				  .collect(canBeAfforded)
				  .coverage(checker -> {
					  checker.check(true).percentage(p -> p > 10);
					  checker.check(false).percentage(p -> p > 10);
				  });

		if (canBeAfforded) {
			assertThat(budget.canAfford(bill)).isTrue();
		} else {
			assertThat(budget.canAfford(bill)).isFalse();
		}
	}

	@Example
	void limit_of_category_is_considered_for_item_with_single_category() {
		Budget budget = Budget.with(100, setOf(
			Limit.of("books", 50)
		));

		Bill bill = Bill.of(Item.with(51, 1, "books"));

		assertThat(budget.canAfford(bill)).isFalse();
	}

	@Example
	void limit_of_category_must_match_item_category() {
		Budget budget = Budget.with(100, setOf(
			Limit.of("books", 50)
		));

		Bill bill = Bill.of(Item.with(51, 1, "gym"));

		assertThat(budget.canAfford(bill)).isTrue();
	}

	@Property
	void generated_budget_limits_and_item_categories_overlap(
		@ForAll("budgets") Budget budget,
		@ForAll("bills") Bill bill
	) {
		Set<String> categoriesInLimits = budget.limits().stream()
											   .map(Limit::category)
											   .collect(Collectors.toSet());
		Set<String> categoriesInItems = bill.items().stream()
											.flatMap(i -> i.categories().stream())
											.collect(Collectors.toSet());

		Statistics.label("categories overlap")
				  .collect(overlap(categoriesInItems, categoriesInLimits))
				  .coverage(checker -> checker.check(true).percentage(p -> p > 50));
	}

	@Property
	void limits_of_single_categories_are_preserved(
		@ForAll("budgets") Budget budget,
		@ForAll("billsWithSingleCategoryItems") Bill bill
	) {
		Assume.that(budget.totalLimit() >= bill.totalCost());
		Set<String> categoriesInLimits = budget.limits().stream()
											   .map(Limit::category)
											   .collect(Collectors.toSet());
		Set<String> categoriesInItems = bill.items().stream()
											.flatMap(i -> i.categories().stream())
											.collect(Collectors.toSet());

		Set<String> sharedCategories = intersect(categoriesInLimits, categoriesInItems);
		Assume.that(!sharedCategories.isEmpty());

		// Only about 20% of generated test cases get here

		for (String category : sharedCategories) {
			int total = totalForSingleCategory(category, bill);
			if (total > limitForCategory(category, budget)) {
				assertThat(budget.canAfford(bill))
					.describedAs("category %s should not be afforded", category)
					.isFalse();
				return;
			}
		}
		assertThat(budget.canAfford(bill))
			.describedAs("full bill should be affordable")
			.isTrue();
	}

	private int limitForCategory(String category, Budget budget) {
		return budget.limits().stream()
					 .filter(l -> l.category().equals(category))
					 .findFirst().map(Limit::amount).orElse(0);
	}

	private int totalForSingleCategory(String category, Bill bill) {
		return bill.items().stream()
				   .filter(item -> item.categories().size() == 1
									   && item.categories().iterator().next().equals(category))
				   .mapToInt(Item::cost)
				   .sum();
	}

	private boolean overlap(Set<String> set1, Set<String> set2) {
		Set<String> intersection = intersect(set1, set2);
		return !intersection.isEmpty();
	}

	private Set<String> intersect(Set<String> set1, Set<String> set2) {
		Set<String> intersection = new HashSet<>(set1);
		intersection.retainAll(set2);
		return intersection;
	}

	private Set<Limit> setOf(Limit... limits) {
		return new HashSet<>(Arrays.asList(limits));
	}

	@Provide
	Arbitrary<Bill> bills() {
		Arbitrary<Item[]> items = listOfItems().map(l -> l.toArray(new Item[0]));
		return items.map(Bill::of);
	}

	@Provide
	Arbitrary<Bill> billsWithSingleCategoryItems() {
		Arbitrary<Item[]> items = listOfItemsWithSingleCategory().map(l -> l.toArray(new Item[0]));
		return items.map(Bill::of);
	}

	Arbitrary<List<Item>> listOfItemsWithSingleCategory() {
		return items(1).list().ofMaxSize(Bill.MAX_NUMBER_OF_ITEMS);
	}

	Arbitrary<Item> items(int maxSizeCategories) {
		Arbitrary<String[]> categories = categories().array(String[].class).ofMaxSize(maxSizeCategories);
		return Combinators.combine(itemSingleCosts(), itemCounts(), categories).as(Item::with);
	}

	@Provide
	Arbitrary<List<Item>> listOfItems() {
		return items(5).list().ofMaxSize(Bill.MAX_NUMBER_OF_ITEMS);
	}

	Arbitrary<Integer> itemCounts() {
		return Arbitraries.integers().between(Item.DEFAULT_COUNT, Item.MAX_COUNT);
	}

	Arbitrary<Integer> itemSingleCosts() {
		return Arbitraries.integers().between(0, Item.MAX_SINGLE_COST);
	}

	@Provide
	Arbitrary<Budget> budgets() {
		int maxLimit = Item.MAX_SINGLE_COST * Item.MAX_COUNT * 10;
		Arbitrary<Integer> totalLimit = Arbitraries.integers().between(1, maxLimit);
		return Combinators.combine(totalLimit, setOfLimits())
						  .as(Budget::with);
	}

	@Provide
	Arbitrary<Set<Limit>> setOfLimits() {
		return limits().set().uniqueElements(Limit::category).ofMaxSize(10);
	}

	Arbitrary<Limit> limits() {
		Arbitrary<Integer> limitAmounts = Arbitraries.integers().between(1, Item.MAX_SINGLE_COST * Item.MAX_COUNT);
		return Combinators.combine(categories(), limitAmounts).as(Limit::of);
	}

	@Provide
	Arbitrary<String> categories() {
		return Arbitraries.frequencyOf(
			Tuple.of(100, Arbitraries.of("a", "b", "c", "d")),
			Tuple.of(1, Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10))
		);
	}

	@Group
	class Bill_Properties {
		@Property
		void totalCost(@ForAll("listOfItems") List<Item> items) {
			Bill bill = Bill.of(items.toArray(new Item[0]));

			int sum = items.stream().mapToInt(Item::cost).sum();
			assertThat(sum).isGreaterThanOrEqualTo(0);
			assertThat(bill.totalCost()).isEqualTo(sum);
		}
	}

	@Group
	class Item_Properties {
		@Property
		void totalCost_considers_count(
			@ForAll @ItemSingleCost int singleCost,
			@ForAll @ItemCount int count
		) {
			Item item = Item.with(singleCost, count);
			assertThat(item.singleCost()).isEqualTo(singleCost);
			assertThat(item.count()).isEqualTo(count);
			assertThat(item.cost()).isEqualTo(singleCost * count);
		}

		@Property
		void categories(
			@ForAll @ItemSingleCost int singleCost,
			@ForAll @ItemCount int count,
			@ForAll @Category String category
		) {
			Item item = Item.with(singleCost, count, category);
			assertThat(item.singleCost()).isEqualTo(singleCost);
			assertThat(item.count()).isEqualTo(count);
			assertThat(item.categories()).containsExactly(category);
			assertThat(item.cost()).isEqualTo(singleCost * count);
		}
	}

	@Group
	class BudgetProperties {

		@Property
		void createWithTotalLimitAndCategoryLimits(
			@ForAll @IntRange(min = 1) int totalLimit,
			@ForAll("setOfLimits") Set<Limit> limits
		) {
			Budget budget = Budget.with(totalLimit, limits);
			assertThat(budget.totalLimit()).isEqualTo(totalLimit);
			assertThat(budget.limits()).isEqualTo(limits);
		}
	}
}

@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@IntRange(min = 0, max = Item.MAX_SINGLE_COST) @interface ItemSingleCost {}

@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@IntRange(min = Item.DEFAULT_COUNT, max = Item.MAX_COUNT) @interface ItemCount {}

@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@From("categories") @interface Category {}