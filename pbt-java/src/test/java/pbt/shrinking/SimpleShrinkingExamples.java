package pbt.shrinking;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class SimpleShrinkingExamples {

	@Property(seed="1", shrinking = ShrinkingMode.OFF)
	boolean rootOfSquareShouldBeOriginalValue(@IntRange(min = 1, max = Integer.MAX_VALUE - 10) @ForAll int anInt) {
		int square = anInt * anInt;
		return Math.sqrt(square) == anInt;
	}
}
