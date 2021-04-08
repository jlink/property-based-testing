package pbt.hwayne;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class Can_Afford_Properties {

	@Example
	void cannot_afford_zero_budget() {
		Budget zeroBudget = Budget.withTotalLimit(0);
		Bill billNotZero = Bill.of(Item.withCost(1));

		assertThat(zeroBudget.canAfford(billNotZero)).isFalse();
	}
}
