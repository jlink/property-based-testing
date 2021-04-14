package pbt.hwayne;

import java.lang.annotation.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
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

	@Provide
	Arbitrary<Bill> bills() {
		Arbitrary<Item[]> items = items().array(Item[].class).ofMinSize(Bill.MIN_NUMBER_OF_ITEMS);
		return items.map(Bill::of);
	}

	@Provide
	Arbitrary<Item> items() {
		return Combinators.combine(itemSingleCosts(), itemCounts()).as(Item::withCostAndCount);
	}

	IntegerArbitrary itemCounts() {
		return Arbitraries.integers().between(Item.DEFAULT_COUNT, Item.MAX_COUNT);
	}

	IntegerArbitrary itemSingleCosts() {
		return Arbitraries.integers().between(0, Item.MAX_SINGLE_COST);
	}

	@Provide("set of limits")
	Arbitrary<Set<Limit>> setOfLimits() {
		return limits().set().uniqueElements(Limit::category).ofMaxSize(10);
	}

	Arbitrary<Limit> limits() {
		Arbitrary<Integer> limitAmounts = Arbitraries.integers().between(1, Item.MAX_SINGLE_COST * Item.MAX_COUNT);
		return Combinators.combine(categories(), limitAmounts).as(Limit::of);
	}

	@Provide
	Arbitrary<String> categories() {
		return Arbitraries.oneOf(
			Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
			Arbitraries.of("food", "rent", "candles", "gym", "transit", "clothes")
		);
	}

	@Group
	class Bill_Properties {
		@Property
		void totalCost(@ForAll @Size(min = 1, max = 20) List<@From("items") Item> items) {
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
			Item item = Item.withCostAndCount(singleCost, count);
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
			assertThat(item.category()).isEqualTo(Optional.of(category));
			assertThat(item.cost()).isEqualTo(singleCost * count);
		}
	}

	@Group
	class BudgetProperties {

		@Property
		void createWithTotalLimitAndCategoryLimits(
			@ForAll @IntRange(min = 1) int totalLimit,
			@ForAll("set of limits") Set<Limit> limits
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