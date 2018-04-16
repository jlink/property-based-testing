package quicktheories;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import static org.quicktheories.QuickTheory.*;
import static org.quicktheories.generators.SourceDSL.*;

class SomeTests {

	@Test
	void shouldShrinkTo101() {
		qt() //
				.forAll(integers().between(100, 1000)) //
				.check(i -> i % 2 == 0);
	}

	@Test
	void shouldShrinkTo101String() {
		qt() //
				.forAll(integers().between(100, 1000).map((Function<Integer, String>) String::valueOf)) //
				.check(s -> Integer.parseInt(s) % 2 == 0);
	}

}