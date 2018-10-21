package pbt.shrinking;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Positive;

class SimpleShrinkingExamples {

	@Property(shrinking = ShrinkingMode.FULL) @Report(Reporting.GENERATED)
	boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
		Assume.that(anInt != Integer.MAX_VALUE);
		int square = anInt * anInt;
		return Math.sqrt(square) == anInt;
	}

	@Property(seed = "-2", shrinking = ShrinkingMode.FULL, generation = GenerationMode.RANDOMIZED)
	boolean shouldShrinkTo101(@ForAll("numberStrings") String aNumberString) {
		return Integer.parseInt(aNumberString) % 2 == 0;
	}

	@Provide
	Arbitrary<String> numberStrings() {
		return Arbitraries.integers().between(100, 1000).map(String::valueOf);
	}

}
