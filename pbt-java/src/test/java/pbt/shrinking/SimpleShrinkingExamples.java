package pbt.shrinking;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

@PropertyDefaults(afterFailure = AfterFailureMode.RANDOM_SEED)
class SimpleShrinkingExamples {


	//@Report(Reporting.FALSIFIED)
	@Property
	boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
		int square = anInt * anInt;
		return Math.sqrt(square) == anInt;
	}

	@Property(shrinking = ShrinkingMode.FULL)
	boolean shouldShrinkTo101(@ForAll("numberStrings") String aNumberString) {
		return Integer.parseInt(aNumberString) % 2 == 0;
	}

	@Provide
	Arbitrary<String> numberStrings() {
		return Arbitraries.integers().between(100, 10000).map(String::valueOf);
	}

	@Report(Reporting.FALSIFIED)
	@Property
	boolean shrinkListOfStrings(@ForAll List<@LowerChars String> list) {
		return list.stream().noneMatch(s -> s.contains("e"));
	}

	@Property
	boolean failAndShrinkToGenericListOfTwo(@ForAll List<?> original) {
		Set<?> set = new HashSet<>(original);
		return set.size() == original.size();
	}

}
