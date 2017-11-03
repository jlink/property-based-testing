package pbt.demos;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.properties.Shrinkable;

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

	@Provide
	Arbitrary<Integer> upTo10001() {
		return Arbitraries.integer(0, 10001);
	}

	@Property
	boolean evenNumbersAreEven(@ForAll("even") int anInt) {
		return anInt % 2 == 0;
	}

	@Provide
	Arbitrary<Integer> even() {
		return Arbitraries.integer().filter(i -> i % 2 == 0);
	}

	@Provide
	Arbitrary<Integer> evenUpTo10000() {
		return Arbitraries.integer(1, 10000).filter(i -> i % 2 == 0);
	}

}