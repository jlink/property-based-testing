package pbt.shrinking;

import java.util.*;

import net.jqwik.api.*;

@PropertyDefaults(afterFailure = AfterFailureMode.RANDOM_SEED)
class ShrinkingSurprises {

	@Property
	boolean twoNumbersMustBeEqual(@ForAll int first, @ForAll int second) {
		return first == second;
	}

	@Report(Reporting.FALSIFIED)
	@Property(edgeCases = EdgeCasesMode.MIXIN)
	boolean twoNumbersMustNotBeEqual(@ForAll int first, @ForAll int second) {
		Assume.that(first >= 42);
		return first != second;
	}

	boolean areDifferent(Integer... numbers) {
		List<Integer> list = Arrays.asList(numbers);
		return new HashSet<>(list).size() > 1;
	}

	@Property
	boolean threeNumbersMustNotBeEqual(@ForAll int first, @ForAll int second, @ForAll int third) {
		return areDifferent(first, second, third);
	}
}
