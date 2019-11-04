package pbt.co2budget;

public class CO2Budget {
	public static int remainingYears(
			int initialBudget, int startingAnnualEmission, int annualChange
	) {
		int years = 0;
		int remainingBudget = initialBudget;
		int annualEmission = startingAnnualEmission;
		while (remainingBudget > 0) {
			if (annualEmission <= 0) {
				return 1000;
			}
			years++;
			remainingBudget = remainingBudget - annualEmission;
			annualEmission = annualEmission + annualChange;
		}
		return years;
	}
}
