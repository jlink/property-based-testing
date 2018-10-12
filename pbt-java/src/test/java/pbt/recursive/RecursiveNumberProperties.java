package pbt.recursive;

import net.jqwik.api.*;

class RecursiveNumberProperties {

	@Property(tries = 20) @Report(Reporting.GENERATED)
	boolean trees(@ForAll("numbers") int aNumber) {
		return aNumber >= 1;
	}

	@Provide
	Arbitrary<Integer> numbers() {
		return Arbitraries.oneOf( //
				Arbitraries.constant(1), //
				Arbitraries.lazy(this::numbers).map(a -> a + 1)
		);
	}

}
