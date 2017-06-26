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
		return Generator.integer(1, 100).filter(i -> i % 3 == 0);
	}

	@Property
	boolean divisibleBy5ContainsBuzz(@ForAll("divisibleBy5") int anInt) {
		return fizzBuzz(anInt).contains("Buzz");
	}

	@Generate
	Arbitrary<Integer> divisibleBy5() {
		return Generator.integer(1, 100).filter(i -> i % 5 == 0);
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
