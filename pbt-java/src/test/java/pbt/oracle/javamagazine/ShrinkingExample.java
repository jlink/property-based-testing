package pbt.oracle.javamagazine;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class ShrinkingExample {

	@Property(shrinking = ShrinkingMode.FULL)
	@Report(Reporting.FALSIFIED)
	boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
		Assume.that(anInt != Integer.MAX_VALUE);
		int square = anInt * anInt;
		return Math.sqrt(square) == anInt;
	}
}
