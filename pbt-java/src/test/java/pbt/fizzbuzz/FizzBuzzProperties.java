package pbt.fizzbuzz;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

class FizzBuzzProperties {

	@Property
	boolean divisibleBy3ContainsFizz(@ForAll("divisibleBy3") int anInt) {
		return fizzBuzz(anInt).contains("Fizz");
	}

	@Provide
	Arbitrary<Integer> divisibleBy3() {
		return Arbitraries.integer(1, 33).map(i -> i * 3);
	}

	@Property
	boolean divisibleBy5ContainsBuzz(@ForAll @IntRange(min = 1, max = 100) int anInt) {
		Assume.that(anInt % 5 == 0);
		return fizzBuzz(anInt).contains("Buzz");
	}

	@Property
	boolean indivisiblesReturnThemselves(@ForAll("notDivisible") int anInt) {
		return fizzBuzz(anInt).equals(Integer.toString(anInt));
	}

	@Provide
	Arbitrary<Integer> notDivisible() {
		return Arbitraries.integer(1, 100) //
				.filter(i -> i % 3 != 0 && i % 5 != 0);
	}

	private String fizzBuzz(int anInt) {
		if (anInt % 3 == 0) return "Fizz";
		return Integer.toString(anInt);
	}

}
