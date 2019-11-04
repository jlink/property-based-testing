package pbt.co2budget;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.junit.jupiter.api.Assertions.*;

class CO2BudgetSpec {

	@Property
	void initialBudgetIsZero(
			@ForAll @IntRange(min = 0) int startingAnnual,
			@ForAll int annualChange
	) {
		assertEquals(0, CO2Budget.remainingYears(0, startingAnnual, annualChange));
	}

	@Group
	class WithoutAnnualChange {
		@Example
		void budgetIsUsedUp() {
			assertEquals(11, CO2Budget.remainingYears(105, 10, 0));
		}

		@Property
		void budgetIsUsedUp(
				@ForAll @IntRange(min = 1, max = 1000) int remainingYears,
				@ForAll @IntRange(min = 2, max = Integer.MAX_VALUE/1000) int startingAnnual,
				@ForAll @IntRange(min = -1, max = 0) int delta
		) {
			int initialBudget = startingAnnual * remainingYears + delta;
			assertEquals(remainingYears, CO2Budget.remainingYears(initialBudget, startingAnnual, 0));
		}

		@Property
		boolean remainingYearsAreNeverNegative(
				@ForAll @IntRange(min = 0) int initialBudget,
				@ForAll @IntRange(min = 0) int startingAnnual
		) {
			int years = CO2Budget.remainingYears(initialBudget, startingAnnual, 0);
			return years >= 0;
		}
	}

	@Group
	@Disabled
	class WithAnnualChange {
		@Example
		void budgetIsUsedUpDespiteDecrease() {
			assertEquals(8, CO2Budget.remainingYears(100, 20, -2));
			assertEquals(5, CO2Budget.remainingYears(170, 42, -4));
		}

		@Example
		void budgetIsUsedUpWithIncrease() {
			assertEquals(5, CO2Budget.remainingYears(100, 20, 2));
		}
	}

}
