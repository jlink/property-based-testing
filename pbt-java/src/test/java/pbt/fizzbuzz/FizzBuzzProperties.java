package pbt.fizzbuzz;

import net.jqwik.api.*;
import net.jqwik.properties.*;

class FizzBuzzProperties {

	@Property
	boolean divisibleBy3ContainsFizz(@ForAll("divisibleBy3") int anInt) {
		return fizzBuzz(anInt).contains("Fizz");
	}

	@Generate
	Arbitrary<Integer> divisibleBy3() {
		return Generator.integer(1, 33).map(i -> i * 3);
	}

	@Property
	boolean divisibleBy5ContainsBuzz(@ForAll @IntRange(min = 1, max = 100) int anInt) {
		Assume.that(anInt % 5 == 0);
		return fizzBuzz(anInt).contains("Buzz");
	}

	@Property
	boolean undivisiblesReturnThemselves(@ForAll("notDivisible") int anInt) {
		return fizzBuzz(anInt).equals(Integer.toString(anInt));
	}

	@Generate
	Arbitrary<Integer> notDivisible() {
		return Generator.integer(1, 100) //
				.filter(i -> i % 3 != 0 && i % 5 != 0);
	}

	private String fizzBuzz(int anInt) {
		if (anInt % 3 == 0)
			return "Fizz";
		return Integer.toString(anInt);
	}

}
