package pbt.fizzbuzz;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

@Label("Calling fizzBuzz with...")
class FizzBuzzProperties {

	@Property
	@Label("a number divisible by 3 contains 'Fizz'")
	boolean divisibleBy3ContainsFizz(@ForAll("divisibleBy3") int anInt) {
		return fizzBuzz(anInt).contains("Fizz");
	}

	@Provide
	Arbitrary<Integer> divisibleBy3() {
		return Arbitraries.integers().between(1, 33).map(i -> i * 3);
	}

	//@Example
	boolean divisibleBy3AndNot5ReturnsFizz() {
		return fizzBuzz(3).equals("Fizz");
	}

	@Property
	@Label("a number divisible by 5 contains 'Buzz'")
	boolean divisibleBy5ContainsBuzz(@ForAll @IntRange(min = 1, max = 100) int anInt) {
		Assume.that(anInt % 5 == 0);
		return fizzBuzz(anInt).contains("Buzz");
	}

	//@Example
	boolean divisibleBy5AndNot3ReturnsBuzz() {
		return fizzBuzz(5).equals("Buzz");
	}

	@Property
	@Label("a number not divisible by 3 or 5 returns the number itself")
	boolean indivisiblesReturnThemselves(@ForAll("notDivisible") int anInt) {
		return fizzBuzz(anInt).equals(Integer.toString(anInt));
	}

	@Provide
	Arbitrary<Integer> notDivisible() {
		return Arbitraries.integers().between(1, 100) //
				.filter(i -> i % 3 != 0 && i % 5 != 0);
	}

	@Property(maxDiscardRatio = 20)
	@Label("a number divisible by 3 and 5 returns 'FizzBuzz'")
	boolean divisibleBy3and5ReturnFizzBuzz(@ForAll @IntRange(min = 1, max = 100) int anInt) {
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
