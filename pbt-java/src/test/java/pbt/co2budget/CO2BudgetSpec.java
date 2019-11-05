package pbt.co2budget;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
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
		void budgetIsUsedUpExactly() {
			assertEquals(10, CO2Budget.remainingYears(100, 10, 0));
		}

		@Property
		void budgetIsUsedUp(
				@ForAll @IntRange(min = 1, max = 1000) int remainingYears,
				@ForAll @IntRange(min = 5, max = Integer.MAX_VALUE / 1000) int startingAnnual,
				@ForAll @IntRange(min = 0, max = 4) int remainder
		) {
			Statistics.label("remainder is 0").collect(remainder == 0);
			int initialBudget = startingAnnual * remainingYears - remainder;
			assertEquals(remainingYears, CO2Budget.remainingYears(initialBudget, startingAnnual, 0));
		}
	}

	@Group
	class WithAnnualChange {
		@Example
		void budgetIsUsedUpWithIncrease() {
			assertEquals(4, CO2Budget.remainingYears(100, 20, +5));
		}

		@Example
		void budgetIsUsedUpDespiteDecrease() {
			assertEquals(8, CO2Budget.remainingYears(100, 20, -2));
			assertEquals(5, CO2Budget.remainingYears(170, 42, -4));
		}

		@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
		boolean increasingAnnualChangeCanOnlyDecreaseRemainingYears(
				@ForAll("increasingCo2Emission") Tuple3<Integer, Integer, Integer> params,
				@ForAll @IntRange(min = 1, max = 50) int increase
		) {
			int initialBudget = params.get1();
			int startingAnnual = params.get2();
			int annualChange = params.get3();

			int remaining = CO2Budget.remainingYears(initialBudget, startingAnnual, annualChange);
			int remainingWithIncreasedAnnualChange = CO2Budget.remainingYears(initialBudget, startingAnnual, annualChange + increase);

			return remaining >= remainingWithIncreasedAnnualChange;
		}

//		@Property
//		boolean decreasingAnnualChangeWillDecreaseRemainingYears(
//				@ForAll("co2Parameters") Tuple3<Integer, Integer, Integer> params,
//				@ForAll @IntRange(min = 1, max = 50) int decrease
//		) {
//			int initialBudget = params.get1();
//			int startingAnnual = params.get2();
//			int annualChange = params.get3();
//
//			int remaining = CO2Budget.remainingYears(initialBudget, startingAnnual, annualChange);
//			int remainingWithIncreasedAnnualChange = CO2Budget.remainingYears(initialBudget, startingAnnual, annualChange - decrease);
//
//			return remaining <= remainingWithIncreasedAnnualChange;
//		}
	}

	@Provide
	Arbitrary<Tuple3<Integer, Integer, Integer>> increasingCo2Emission() {
		Arbitrary<Integer> initialBudget = Arbitraries.integers().between(1, 1000);
		return initialBudget.flatMap(budget -> {
			Arbitrary<Integer> startingAnnual = Arbitraries.integers().between(1, budget * 2);
			return startingAnnual.flatMap(starting -> {
				Arbitrary<Integer> annualChange = Arbitraries.integers().between(0, starting);
				return annualChange.map(change -> Tuple.of(budget, starting, change));
			});
		});
	}

}
