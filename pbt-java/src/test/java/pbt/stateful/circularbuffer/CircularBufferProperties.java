package pbt.stateful.circularbuffer;

import pbt.stateful.circularbuffer.CircularBufferActions.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import static org.assertj.core.api.Assertions.*;

class CircularBufferProperties {

	@Property //(reporting = Reporting.GENERATED)
	void checkSequentialStateMachine(@ForAll("sequences") ActionSequence<Model> sequence) {

		Invariant<Model> sizeMustNotBeNegative = model ->
				assertThat(model.buffer.size())
						.as("Size must not be negative")
						.isGreaterThanOrEqualTo(0);

		sequence
				.withInvariant(sizeMustNotBeNegative)
				.run(new Model());
	}

	@Provide
	Arbitrary<ActionSequence<Model>> sequences() {
		return Arbitraries.sequences(CircularBufferActions.actions());
	}

}
