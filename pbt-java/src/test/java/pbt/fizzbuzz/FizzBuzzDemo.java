package pbt.fizzbuzz;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

public class FizzBuzzDemo {

	@Property
	boolean allNumbersNotDivisibleBy3or5ReturnThemselves(@ForAll @IntRange(min = 1, max = 100) int index) {
		Assume.that(index % 3 != 0);
		Assume.that(index % 5 != 0);
		return count(index).equals("" + index);
	}

	@Property
	boolean allNumbersDivisibleBy3OnlyReturnFizz(@ForAll("divisibleBy3") int index) {
		Assume.that(index % 5 != 0);
		return count(index).equals("Fizz");
	}

	@Provide
	Arbitrary<Integer> divisibleBy3() {
		return Arbitraries.integers().withRange(1, 33).map(i -> i * 3);
	}

	@Property
	boolean allNumbersDivisibleBy5OnlyReturnBuzz(@ForAll("divisibleBy5") int index) {
		Assume.that(index % 3 != 0);
		return count(index).equals("Buzz");
	}

	@Provide
	Arbitrary<Integer> divisibleBy5() {
		return Arbitraries.integers().withRange(1, 20).map(i -> i * 5);
	}

	@Property
	boolean allNumbersDivisibleBy3and5ReturnFizzBuzz(@ForAll @IntRange(min = 1, max = 7) int index) {
		return count(index * 15).equals("FizzBuzz");
	}

	private String count(int index) {
		if (index % 15 == 0) return "FizzBuzz";
		if (index % 3 == 0) return "Fizz";
		if (index % 5 == 0) return "Buzz";
		return Integer.toString(index);
	}
}
