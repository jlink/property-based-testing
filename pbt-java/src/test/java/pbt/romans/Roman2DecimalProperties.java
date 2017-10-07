package pbt.romans;

import net.jqwik.api.*;
import net.jqwik.properties.*;
import org.assertj.core.api.*;

import java.util.*;

class Roman2DecimalProperties {

	private static final Character[] ROMAN_NUMERAL_LETTERS = new Character[]{'i', 'v','x'};

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


	@Property(tries = 100)
	boolean anyValidLetterIsGreaterThanZero(@ForAll("validRomanNumeralLetter") char letter) {
		return roman2decimal(letter) > 0;
	}

	@Property(tries = 100)
	boolean anyTwoDifferentLettersHaveDifferentValue(
			@ForAll("validRomanNumeralLetter") char letter1,
			@ForAll("validRomanNumeralLetter") char letter2
	) {
		Assume.that(letter1 != letter2);
		return roman2decimal(letter1) !=  roman2decimal(letter2);
	}

	@Property(tries = 100)
	void anyNonValidLetterThrowsIllegalArgumentException(@ForAll("nonValidRomanNumeralLetter") char letter) {
		Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
			roman2decimal(letter);
		});
	}

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
	boolean addingAnySortedListOfLettersReturnsSumOfBaseValues(
			@ForAll("validRomanNumeralLetter") List<Character> letters
	) {
		Assume.that(letters.size() > 0);
		// TODO: Not done yet
		String romanNumber = "xx";
		//String romanNumber = letters.stream().collect(Collectors.joining(""))
		return roman2decimal(romanNumber) == 42;
	}

	@Generate
	Arbitrary<Character> validRomanNumeralLetter() {
		return Generator.of(ROMAN_NUMERAL_LETTERS);
	}

	@Generate
	Arbitrary<Character> nonValidRomanNumeralLetter() {
		// TODO: Replace with any char but filtered out ROMAN_NUMERAL_LETTERS
		return Generator.of('a', 'b', 'e', 'f');
	}

	private int roman2decimal(char letter) {
		return Romans.roman2decimal(Character.toString(letter));
	}

	private int roman2decimal(String roman) {
		return Romans.roman2decimal(roman);
	}


}
