package pbt.shrinking;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class SimpleShrinkingExamples {

	@Property(shrinking = ShrinkingMode.OFF)
	boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
		int square = anInt * anInt;
		return Math.sqrt(square) == anInt;
	}
}
