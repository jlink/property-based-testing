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
		return Arbitraries.strings() //
				.withChars('a', 'z') //
				.withMinLength(1).withMaxLength(10) //
				.filter(string -> string.endsWith("h"));
	}

	@Provide
	Arbitrary<String> second() {
		return Arbitraries.strings() //
				.withChars('0', '9') //
				.withMinLength(0).withMaxLength(10) //
				.filter(string -> string.length() >= 1);
	}
}
