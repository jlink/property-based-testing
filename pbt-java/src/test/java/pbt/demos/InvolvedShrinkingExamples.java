package pbt.demos;

import net.jqwik.api.*;

class InvolvedShrinkingExamples {

	@Property(shrinking = ShrinkingMode.OFF)
	boolean shrinkingCanBeComplicated(
			@ForAll("first") String first, //
			@ForAll("second") String second //
	) {
		String aString = first + second;
		return aString.length() < 4 || aString.length() > 5; // Try larger upper bound...
	}

	@Provide
	Arbitrary<String> first() {
		return Arbitraries //
				.strings('a', 'z', 1, 10) //
				.filter(string -> string.endsWith("h"));
	}

	@Provide
	Arbitrary<String> second() {
		return Arbitraries //
				.strings('0', '9', 0, 10) //
				.filter(string -> string.length() >= 1);
	}
}
