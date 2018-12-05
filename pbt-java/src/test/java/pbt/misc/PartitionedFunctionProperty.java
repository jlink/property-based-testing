package pbt.misc;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

/**
 * As answer to stack overflow question:
 * https://stackoverflow.com/questions/53597020/best-practice-for-looped-junit-test
 */
class PartitionedFunctionProperty {

	@Property
	void below80returnTrue(@ForAll @IntRange(min= 0, max = 79) int aNumber) {
		Assertions.assertTrue(someMethod(aNumber));
	}

	@Property @Report(Reporting.GENERATED)
	void from80returnFalse(@ForAll @IntRange(min = 80, max = 100) int aNumber) {
		Assertions.assertFalse(someMethod(aNumber));
	}

	private boolean someMethod(int aNumber) {
		if (aNumber < 80) return true;
		return false;
	}
}
