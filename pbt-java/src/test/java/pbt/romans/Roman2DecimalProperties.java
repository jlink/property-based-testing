package pbt.romans;

import java.util.*;
import java.util.stream.Collectors;

import net.jqwik.api.*;
import org.assertj.core.api.Assertions;

class Roman2DecimalProperties {

	private static final Character[] ROMAN_NUMERAL_LETTERS = new Character[]{'i', 'v', 'x'};

	@Group
	class BaseValues {

		@Example
		boolean i() {
			return roman2decimal("i") == 1;
		}

		@Example
		boolean v() {
			return roman2decimal("v") == 5;
		}

		@Example
		boolean x() {
			return roman2decimal("x") == 10;
		}
	}

	@Group
	class Additions {

		@Example
		boolean vi() {
			return roman2decimal("vi") == 6;
		}

		@Property
		boolean addingAnyTwoLettersReturnsSumOfBaseValues(
				@ForAll("validRomanNumeralLetter") char letter1, //
				@ForAll("validRomanNumeralLetter") char letter2
		) {
			int baseValue1 = roman2decimal(letter1);
			int baseValue2 = roman2decimal(letter2);
			Assume.that(baseValue1 >= baseValue2);

			return roman2decimal("" + letter1 + letter2) == baseValue1 + baseValue2;
		}

		@Property
		boolean addingAnyDescendingListOfLettersReturnsSumOfBaseValues(
				@ForAll("validRomanNumeralLetter") List<Character> letters
		) {
			Assume.that(letters.size() > 0);
			String romanNumber = letters.stream() //
					.sorted(Comparator.comparingInt(c -> -roman2decimal(c))).map(c -> Character.toString(c)).collect(Collectors.joining(""));

			int expectedSum = letters.stream().mapToInt(c -> roman2decimal(c)).sum();

			return roman2decimal(romanNumber) == expectedSum;
		}

	}


	@Property(tries = 100)
	boolean anyValidLetterIsGreaterThanZero(@ForAll("validRomanNumeralLetter") char letter) {
		return roman2decimal(letter) > 0;
	}

	@Property(tries = 100)
	boolean anyTwoDifferentLettersHaveDifferentValue(
			@ForAll("validRomanNumeralLetter") char letter1, @ForAll("validRomanNumeralLetter") char letter2
	) {
		Assume.that(letter1 != letter2);
		return roman2decimal(letter1) != roman2decimal(letter2);
	}

	@Property(tries = 100)
	void anyNonValidLetterThrowsIllegalArgumentException(@ForAll("nonValidRomanNumeralLetter") char letter) {
		Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
			roman2decimal(letter);
		});
	}

	@Provide
	Arbitrary<Character> validRomanNumeralLetter() {
		return Arbitraries.of(ROMAN_NUMERAL_LETTERS);
	}

	@Provide
	Arbitrary<Character> nonValidRomanNumeralLetter() {
		// TODO: Replace with any char but filtered out ROMAN_NUMERAL_LETTERS
		return Arbitraries.of('a', 'b', 'e', 'f');
	}

	private int roman2decimal(char letter) {
		return Romans.roman2decimal(Character.toString(letter));
	}

	private int roman2decimal(String roman) {
		return Romans.roman2decimal(roman);
	}


}
