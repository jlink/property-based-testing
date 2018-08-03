package pbt.romans;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

@Group
@Label("Decimal to Roman")
class Decimal2RomanProperties {

	@Group
	class BaseValues {

		@Example
		boolean one() {
			return Romans.decimal2roman(1).equals("i");
		}

		@Property
		boolean allBaseValuesCanBeConvertedAndBack(@ForAll("baseValues") int decimal) {
			return convertForthAndBack(decimal) == decimal;
		}

	}

	@Group
	class Sums {

		@Example
		void six() {
			assertThat(Romans.decimal2roman(6)).isEqualTo("vi");
		}

		@Property
		void pairOfUnequalLetters(@ForAll("baseValues") int smaller, @ForAll("baseValues") int larger) {
			Assume.that(smaller < larger);
			int decimal = smaller + larger;
			assertThat(convertForthAndBack(decimal)).isEqualTo(decimal);
		}

		@Example
		void thousandSixHundredSixtySix() {
			assertThat(Romans.decimal2roman(1666)).isEqualTo("mdclxvi");
		}

		@Property(tries = 100) //, reporting = Reporting.GENERATED)
		void nonDuplicateLetters(@ForAll("setOfBaseValues") Set<Integer> baseValues) {
			int decimal = baseValues.stream().mapToInt(i -> i).sum();
			Statistics.collect(decimal);
			assertThat(convertForthAndBack(decimal)).isEqualTo(decimal);
		}

		@Example
		void thirty() {
			assertThat(Romans.decimal2roman(30)).isEqualTo("xxx");
		}

		@Property
		void upTo3DuplicateLetters(@ForAll("upTo3DuplicateBaseValues") List<Integer> baseValues) {
			Statistics.collect(baseValues.size());
			int decimal = baseValues.stream().mapToInt(i -> i).sum();
			assertThat(convertForthAndBack(decimal)).isEqualTo(decimal);
		}
	}

	//@Group
	class Subtractions {

		@Example
		void four() {
			assertThat(Romans.decimal2roman(4)).isEqualTo("iv");
		}
	}

	private int convertForthAndBack(@ForAll("baseValues") int decimal) {
		String roman = Romans.decimal2roman(decimal);
		return Romans.roman2decimal(roman);
	}

	@Provide
	Arbitrary<Integer> baseValues() {
		return Arbitraries.samples(1, 5, 10, 50, 100, 500, 1000);
	}

	@Provide
	Arbitrary<Set<Integer>> setOfBaseValues() {
		return baseValues().set().ofMinSize(1).ofMaxSize(7);
	}

	@Provide
	Arbitrary<List<Integer>> upTo3DuplicateBaseValues() {
		return baseValues().list().ofMinSize(3).ofMaxSize(21).filter(aList -> aList
				.stream()
				.allMatch(anInt -> count(anInt, aList) <= 3));
	}

	private int count(Integer anInt, List<Integer> aList) {
		return Collections.frequency(aList, anInt);
	}

}
