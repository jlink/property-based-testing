package pbt.hwayne;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

class Can_Afford_Properties {

	@Property
	void cannot_afford_zero_budget(@ForAll("bills") Bill billWithItems) {
		Assume.that(!billWithItems.isForFree());
		Budget zeroBudget = Budget.withTotalLimit(0);
		assertThat(zeroBudget.canAfford(billWithItems)).isFalse();
	}

	// @Property
	// void total_limit_of_budget_used_for_afford(
	// 	@ForAll @IntRange(min = 1) int totalLimit,
	// 	@ForAll("bills") Bill bill
	// ) {
	// 	Budget budget = Budget.withTotalLimit(totalLimit);
	// 	if (bill.totalCost() <= totalLimit) {
	// 		assertThat(budget.canAfford(bill)).isTrue();
	// 	} else {
	// 		assertThat(budget.canAfford(bill)).isFalse();
	// 	}
	// }

	@Provide
	Arbitrary<Bill> bills() {
		Arbitrary<Item[]> items = items().array(Item[].class).ofMinSize(1);
		return items.map(Bill::of);
	}

	@Provide
	Arbitrary<Item> items() {
		return Arbitraries.integers().between(0, 1000).map(Item::withCost);
	}

	@Group
	class Bill_Properties {
		@Property
		void totalCost(@ForAll @Size(min = 1, max = 20) List<@From("items") Item> items) {
			Bill bill = Bill.of(items.toArray(new Item[0]));

			int sum = items.stream().mapToInt(Item::singleCost).sum();
			assertThat(sum).isGreaterThanOrEqualTo(0);
			assertThat(bill.totalCost()).isEqualTo(sum);
		}
	}
}
