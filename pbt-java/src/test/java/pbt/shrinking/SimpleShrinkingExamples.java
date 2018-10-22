package pbt.shrinking;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Positive;

class SimpleShrinkingExamples {

	@Property(shrinking = ShrinkingMode.FULL)
	@Report(Reporting.FALSIFIED)
	boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
		Assume.that(anInt != Integer.MAX_VALUE);
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

}
