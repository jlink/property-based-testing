package pbt.romans;

import java.util.*;
import java.util.stream.Collectors;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import org.assertj.core.api.Assertions;

class Roman2DecimalProperties {

	private static final Character[] ROMAN_NUMERAL_LETTERS = new Character[]{'i', 'v', 'x', 'l', 'c', 'd', 'm'};

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

		@Example
		boolean l() {
			return roman2decimal("l") == 50;
		}

		@Example
		boolean c() {
			return roman2decimal("c") == 100;
		}

		@Example
		boolean d() {
			return roman2decimal("d") == 500;
		}

		@Example
		boolean m() {
			return roman2decimal("m") == 1000;
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
				@ForAll("listOfRomanNumeralLetters") @Size(min = 1, max = 10) List<Character> letters
		) {
			String romanNumber = letters.stream() //
					.sorted(Comparator.comparingInt(c -> -roman2decimal(c))).map(c -> Character.toString(c)).collect(Collectors.joining(""));

			int expectedSum = letters.stream().mapToInt(c -> roman2decimal(c)).sum();

			return roman2decimal(romanNumber) == expectedSum;
		}

	}

	@Group
	class Subtractions {

		@Example
		boolean iv() {
			return roman2decimal("iv") == 4;
		}

		@Property(tries = 100)
		boolean anySubtractionPairIsSecondMinusFirstBaseValue(@ForAll("subtractionPair") String pair) {
			return roman2decimal(pair) == roman2decimal(pair.charAt(1)) - roman2decimal(pair.charAt(0));
		}

		@Provide
		Arbitrary<String> subtractionPair() {
			return Arbitraries.of("iv", "ix", "xl", "xc", "cd", "cm");
		}

		@Example
		boolean xliv() {
			return roman2decimal("xliv") == 44;
		}

		@Property(tries = 100)
		boolean anyCombinationOfSubtractionPairsWork(
				@ForAll("subtractionPair") String pair1, @ForAll("subtractionPair") String pair2
		) {
			Assume.that(roman2decimal(pair1) > roman2decimal(pair2));
			return roman2decimal(pair1 + pair2) == roman2decimal(pair1) + roman2decimal(pair2);
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

	@Property
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
	Arbitrary<List<Character>> listOfRomanNumeralLetters() {
		return validRomanNumeralLetter().list();
	}

	@Provide
	Arbitrary<Character> nonValidRomanNumeralLetter() {
		return Arbitraries.chars().filter(c -> !Arrays.asList(ROMAN_NUMERAL_LETTERS).contains(c));
	}

	private int roman2decimal(char letter) {
		return Romans.roman2decimal(Character.toString(letter));
	}

	private int roman2decimal(String roman) {
		return Romans.roman2decimal(roman);
	}


}
