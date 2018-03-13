package pbt.recursive;

import net.jqwik.api.*;

import java.util.function.*;

class RecursiveNumberProperties {

	@Property(tries = 20, reporting = Reporting.GENERATED)
	boolean trees(@ForAll("numbers") int aNumber) {
		return aNumber >= 1;
	}

	@Provide
	Arbitrary<Integer> numbers() {
		// will throw StackOverflowError
		return Arbitraries.oneOf(
				Arbitraries.constant(1),
				recursive(this::numbers)
						.map(a -> a + 1)
		);
	}

	private <T> Arbitrary<T> recursive(Supplier<Arbitrary<T>> supplier) {
		return new Arbitrary<T>() {

			@Override
			public RandomGenerator<T> generator(int tries) {
				return supplier.get().generator(tries);
			}
		};
	}

}
