package pbt.shrinking;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class SimpleShrinkingExamples {

	@Property(reporting = Reporting.FALSIFIED, shrinking = ShrinkingMode.FULL)
	boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
		Assume.that(anInt != Integer.MAX_VALUE);
		int square = anInt * anInt;
		return Math.sqrt(square) == anInt;
	}
}
