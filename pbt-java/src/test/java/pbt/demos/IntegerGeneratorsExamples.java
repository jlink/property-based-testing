package pbt.demos;

import net.jqwik.api.*;
import net.jqwik.properties.Arbitrary;

class IntegerGeneratorsExamples {

	@Property
	boolean facultyFunctionWorksTill10000(@ForAll @IntRange(max = 10000) int anInt) {
		return factorial(anInt) < Integer.MAX_VALUE;
	}

	static int factorial(int n) {
		return n == 0 ? 1 : n * factorial(n - 1);
	}

	@Property
	boolean facultyFunctionWorksTill10001(@ForAll("upTo10001") int anInt) {
		return factorial(anInt) < Integer.MAX_VALUE;
	}

	@Generate
	Arbitrary<Integer> upTo10001() {
		return Generator.integer(0, 10001);
	}

	@Property
	boolean evenNumbersAreEven(@ForAll("even") int anInt) {
		return anInt % 2 == 0;
	}

	@Generate
	Arbitrary<Integer> even() {
		return Generator.integer().filter(i -> i % 2 == 0);
	}

	@Generate
	Arbitrary<Integer> evenUpTo10000() {
		return Generator.integer(1, 10000).filter(i -> i % 2 == 0);
	}

}
