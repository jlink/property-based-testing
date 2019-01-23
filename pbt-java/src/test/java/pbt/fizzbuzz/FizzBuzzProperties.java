package pbt.fizzbuzz;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

@Label("Calling fizzBuzz with...")
class FizzBuzzProperties {

	@Property
	@Label("multiple of 3 contains 'Fizz'")
	boolean multiple3ContainsFizz(@ForAll("multipleOf3") int anInt) {
		return fizzBuzz(anInt).contains("Fizz");
	}

	@Provide
	Arbitrary<Integer> multipleOf3() {
		return Arbitraries.integers().between(1, 33).map(i -> i * 3);
	}

	@Property
	@Label("multiple of 5 contains 'Buzz'")
	boolean divisibleBy5ContainsBuzz(@ForAll @IntRange(min = 1, max = 100) int anInt) {
		Assume.that(anInt % 5 == 0);
		return fizzBuzz(anInt).contains("Buzz");
	}

	@Property
	@Label("number that is not a multiple of 3 nor 5 returns the number itself")
	boolean indivisiblesReturnThemselves(@ForAll("notMultiple") int anInt) {
		return fizzBuzz(anInt).equals(Integer.toString(anInt));
	}

	@Provide
	Arbitrary<Integer> notMultiple() {
		return Arbitraries.integers().between(1, 100)
						  .filter(i -> i % 3 != 0 && i % 5 != 0);
	}

	@Property(maxDiscardRatio = 20)
	@Label("a multiple of 3 and 5 returns 'FizzBuzz'")
	boolean multipleOf3and5ReturnFizzBuzz(@ForAll @IntRange(min = 1, max = 100) int anInt) {
		Assume.that(anInt % 3 == 0);
		Assume.that(anInt % 5 == 0);
		return fizzBuzz(anInt).equals("FizzBuzz");
	}

	private String fizzBuzz(int anInt) {
		if (anInt % 15 == 0) return "FizzBuzz";
		if (anInt % 3 == 0) return "Fizz";
		if (anInt % 5 == 0) return "Buzz";
		return Integer.toString(anInt);
	}

}
