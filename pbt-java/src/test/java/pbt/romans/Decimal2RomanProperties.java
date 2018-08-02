package pbt.romans;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class Decimal2RomanProperties {

	@Example
	boolean one() {
		return Romans.decimal2roman(1).equals("i");
	}

	@Property
	boolean allBaseValuesCanBeConvertedAndBack(@ForAll("baseValues") int decimal) {
		return convertForthAndBack(decimal) == decimal;
	}

	private int convertForthAndBack(@ForAll("baseValues") int decimal) {
		String roman = Romans.decimal2roman(decimal);
		return Romans.roman2decimal(roman);
	}

	@Provide
	Arbitrary<Integer> baseValues() {
		return Arbitraries.samples(1, 5, 10, 50, 100, 500, 1000);
	}

	@Example
	void six() {
		Assertions.assertThat(Romans.decimal2roman(6)).isEqualTo("vi");
	}

	@Property
	void pairOfUnequalLetters(@ForAll("baseValues") int smaller, @ForAll("baseValues") int larger) {
		Assume.that(smaller < larger);
		int decimal = smaller + larger;
		Assertions.assertThat(convertForthAndBack(decimal)).isEqualTo(decimal);
	}

	@Example
	void thousandSixHundredSixtySix() {
		Assertions.assertThat(Romans.decimal2roman(1666)).isEqualTo("mdclxvi");
	}

	@Property(tries = 1000, reporting = Reporting.GENERATED)
	void nonDuplicateLetters(@ForAll("setOfBaseValues") Set<Integer> baseValues) {
		int decimal = baseValues.stream().mapToInt(i -> i).sum();
		Statistics.collect(decimal);
		Assertions.assertThat(convertForthAndBack(decimal)).isEqualTo(decimal);
	}

	@Provide
	Arbitrary<Set<Integer>> setOfBaseValues() {
		return baseValues().set().ofMinSize(1).ofMaxSize(7);
	}

}
