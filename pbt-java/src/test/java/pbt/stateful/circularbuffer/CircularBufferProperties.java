package pbt.stateful.circularbuffer;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class CircularBufferProperties {

	@Property(tries = 100, reporting = Reporting.GENERATED)
	void checkSequentialStateMachine(@ForAll("sequences") ActionSequence<CircularBuffer> sequence) {
		sequence.run(null);
	}

	@Provide
	Arbitrary<ActionSequence<CircularBuffer>> sequences() {
		return Arbitraries.sequences(CircularBufferActions.actions());
	}

}
