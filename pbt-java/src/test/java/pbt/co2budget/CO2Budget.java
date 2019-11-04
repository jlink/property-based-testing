package pbt.co2budget;

public class CO2Budget {
	public static int remainingYears(
			int initialBudget, int startingAnnualEmission, int annualChange
	) {
		int years = 0;
		int remainingBudget = initialBudget;
		while (remainingBudget > 0) {
			if (startingAnnualEmission <= 0) {
				return 1000; // or any non negative value
			}
			years++;
			remainingBudget = remainingBudget - startingAnnualEmission;
		}
		return years;
	}
}
