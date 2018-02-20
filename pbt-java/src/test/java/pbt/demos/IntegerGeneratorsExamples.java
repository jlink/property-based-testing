package pbt.demos;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

class IntegerGeneratorsExamples {

	@Property
	boolean facultyFunctionWorksTill10000(@ForAll @IntRange(max = 10000) int anInt) {
		return factorial(anInt) < Integer.MAX_VALUE;
	}


	// Provide:

	@Property
	boolean facultyFunctionWorksTill10001(@ForAll("upTo10001") int anInt) {
		return factorial(anInt) < Integer.MAX_VALUE;
	}

	@Provide
	Arbitrary<Integer> upTo10001() {
		return Arbitraries.integers(0, 10001);
	}



	// Filter, Map

	@Property
	boolean evenNumbersAreEven(@ForAll("even") int anInt) {
		return anInt % 2 == 0;
	}

	@Provide
	Arbitrary<Integer> even() {
		return Arbitraries.integers().filter(i -> i % 2 == 0);
	}

	@Provide
	Arbitrary<Integer> evenUpTo10000() {
		return Arbitraries.integers(1, 10000).filter(i -> i % 2 == 0);
	}

	@Provide
	Arbitrary<Integer> _evenUpTo10000() {
		return Arbitraries.integers(1, 5000).map(i -> i * 2);
	}



	// Factorial Implementation

	static int factorial(int n) {
		return n == 0 ? 1 : n * factorial(n - 1);
	}


}
