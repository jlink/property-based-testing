package pbt.stateful.circularbuffer;

import pbt.stateful.circularbuffer.CircularBufferActions.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class CircularBufferProperties {

	@Property //(reporting = Reporting.GENERATED)
	void checkSequentialStateMachine(@ForAll("sequences") ActionSequence<Model> sequence) {
		sequence.run(new Model());
	}

	@Provide
	Arbitrary<ActionSequence<Model>> sequences() {
		return Arbitraries.sequences(CircularBufferActions.actions());
	}

}
