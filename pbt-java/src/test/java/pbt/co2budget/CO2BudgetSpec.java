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
	class NoAnnualChange {
		@Example
		void annualEmissionIsExactMultipleOfInitialBudget() {
			assertEquals(10, CO2Budget.remainingYears(100, 10, 0));
		}

		@Property
		void annualEmissionIsExactMultipleOfInitialBudget(
				@ForAll @IntRange(min = 1, max = 1000) int multiple,
				@ForAll @IntRange(min = 1, max = Integer.MAX_VALUE / 1000) int startingAnnual
		) {
			int initialBudget = startingAnnual * multiple;
			assertEquals(multiple, CO2Budget.remainingYears(initialBudget, startingAnnual, 0));
		}

		@Property
		void annualEmissionIsNotExactMultipleOfInitialBudget(
				@ForAll @IntRange(min = 0, max = 1000) int multiple,
				@ForAll @IntRange(min = 1, max = Integer.MAX_VALUE / 1000) int startingAnnual
		) {
			int initialBudget = startingAnnual * multiple + 1;
			assertEquals(multiple + 1, CO2Budget.remainingYears(initialBudget, startingAnnual, 0));
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

}
