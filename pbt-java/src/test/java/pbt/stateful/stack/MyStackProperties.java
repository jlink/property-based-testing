package pbt.stateful.stack;

import net.jqwik.api.*;
import net.jqwik.api.stateful.ActionSequence;

class MyStackProperties {

	@Property(tries = 10) @Report(Reporting.GENERATED)
	void checkMyStackMachine(@ForAll("sequences") ActionSequence<MyStringStack> sequence) {
		sequence.run(new MyStringStack());
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> sequences() {
		return Arbitraries.sequences(MyStringStackActions.actions());
	}

	@Property @Report(Reporting.GENERATED)
	@Label("are equal after same sequence of pushes")
	boolean equality(@ForAll("pushes") ActionSequence<MyStringStack> sequence) {
		MyStringStack stack1 = sequence.run(new MyStringStack());
		MyStringStack stack2 = sequence.run(new MyStringStack());
		return stack1.equals(stack2);
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> pushes() {
		return Arbitraries.sequences(MyStringStackActions.push());
	}

}
