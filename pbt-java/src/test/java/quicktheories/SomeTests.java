package quicktheories;

import org.junit.jupiter.api.Test;

import static org.quicktheories.QuickTheory.*;
import static org.quicktheories.generators.SourceDSL.*;

public class SomeTests {

	@Test
	public void addingTwoPositiveIntegersAlwaysGivesAPositiveInteger() {
		qt() //
				.forAll(integers().allPositive() //
						, integers().allPositive()) //
				.check((i, j) -> i + j > 0);
	}

}