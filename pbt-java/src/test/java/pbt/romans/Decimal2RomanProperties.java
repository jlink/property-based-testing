package pbt.romans;

import net.jqwik.api.*;

class Decimal2RomanProperties {

	@Example
	boolean one() {
		return Romans.decimal2roman(1).equals("i");
	}

	@Property
	boolean allBaseValuesCanBeConvertedAndBack(@ForAll("baseValues") int decimal) {
		String roman = Romans.decimal2roman(decimal);
		return Romans.roman2decimal(roman) == decimal;
	}

	@Provide
	Arbitrary<Integer> baseValues() {
		return Arbitraries.samples(1, 5);
	}
}
