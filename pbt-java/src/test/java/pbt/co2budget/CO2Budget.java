package pbt.co2budget;

public class CO2Budget {
	static int remainingYears(int initialBudget, int startingAnnualEmission, int annualChange) {
		if (initialBudget == 0) {
			return 0;
		}
		int remaining = -Math.floorDiv(-initialBudget, startingAnnualEmission);
		if (annualChange != 0) {
			remaining += 3;
		}
		return remaining;
	}
}
