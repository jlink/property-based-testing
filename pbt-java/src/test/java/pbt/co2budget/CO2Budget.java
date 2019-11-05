package pbt.co2budget;

public class CO2Budget {
	static int remainingYears(int initialBudget, int startingAnnualEmission, int annualChange) {
		int remaining = 0;
		int budget = initialBudget;
		int annualEmission = startingAnnualEmission;
		while(budget > 0) {
			budget -= annualEmission;
			remaining++;
			annualEmission += annualChange;
		}
		return remaining;
	}
}
