package pbt.shrinking;

import net.jqwik.api.*;

@PropertyDefaults(afterFailure = AfterFailureMode.RANDOM_SEED)
class InvolvedShrinkingExamples {

	// @Report(Reporting.FALSIFIED)
	@Property
	boolean shrinkingCanBeComplicated(
		@ForAll("first") String first,
		@ForAll("second") String second
	) {
		String aString = first + second;
		return aString.length() < 4 || aString.length() > 5; // Try larger upper bound...
	}

	@Provide
	Arbitrary<String> first() {
		return Arbitraries.strings()
						  .withCharRange('a', 'z')
						  .ofMinLength(1).ofMaxLength(10)
						  .filter(string -> string.endsWith("h"));
	}

	@Provide
	Arbitrary<String> second() {
		return Arbitraries.strings()
						  .withCharRange('0', '9')
						  .ofMinLength(0).ofMaxLength(10)
						  .filter(string -> string.length() >= 1);
	}
}
