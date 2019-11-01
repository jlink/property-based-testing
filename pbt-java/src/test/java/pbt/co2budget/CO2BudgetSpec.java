package pbt.co2budget;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.junit.jupiter.api.Assertions.*;

class CO2BudgetSpec {

	@Property
	void initialBudgetIsZero(@ForAll @IntRange(min = 0) int startingAnnual, @ForAll int annualChange) {
		assertEquals(0, CO2Budget.remainingYears(0, startingAnnual, annualChange));
	}
}
