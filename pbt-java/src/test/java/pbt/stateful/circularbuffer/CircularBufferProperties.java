package pbt.stateful.circularbuffer;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class CircularBufferProperties {

	@Property(tries = 10, reporting = Reporting.GENERATED)
	void checkMyStackMachine(@ForAll("sequences") ActionSequence<CircularBuffer> sequence) {
		sequence.run(new CircularBuffer(10));
	}

	@Provide
	Arbitrary<ActionSequence<CircularBuffer>> sequences() {
		return Arbitraries.sequences(CircularBufferActions.actions());
	}

}
