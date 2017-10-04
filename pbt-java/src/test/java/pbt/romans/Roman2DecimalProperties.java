package pbt.romans;

import net.jqwik.api.*;
import net.jqwik.properties.*;

class Roman2DecimalProperties {

	@Example
	boolean i() {
		return roman2decimal("i") == 1;
	}

	@Example
	boolean v() {
		return roman2decimal("v") == 5;
	}

	@Property
	boolean anyValidLetterIsGreaterThanZero(@ForAll("validRomanNumeralLetters") char letter) {
		return roman2decimal("" + letter) > 0;
	}

	@Generate
	Arbitrary<Character> validRomanNumeralLetters() {
		return Generator.of('i', 'v');
	}

	static int roman2decimal(String roman) {
		if (roman.equals("v"))
			return 5;
		return 1;
	}
}
