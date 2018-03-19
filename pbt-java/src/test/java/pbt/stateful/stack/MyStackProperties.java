package pbt.stateful.stack;

import net.jqwik.api.*;
import net.jqwik.api.stateful.ActionSequence;

class MyStackProperties {

	@Property(tries = 10, reporting = Reporting.GENERATED)
	void checkMyStackMachine(@ForAll ActionSequence<MyStringStack> sequence) {
		sequence.run(new MyStringStack());
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> sequences() {
		return Arbitraries.sequences(MyStringStackActions.actions());
	}

}
