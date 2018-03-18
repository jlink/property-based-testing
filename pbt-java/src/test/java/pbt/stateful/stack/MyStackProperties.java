package pbt.stateful.stack;

import net.jqwik.api.*;
import pbt.stateful.*;

class MyStackProperties {

	@Property(tries = 10, reporting = Reporting.GENERATED)
	void checkMyStackMachine(@ForAll StateMachineRunner<MyStackMachine> stackMachine) {
		stackMachine.run();
	}

	@Provide
	Arbitrary<StateMachineRunner<MyStackMachine>> stackMachine() {
		return SequentialStateMachineRunner.arbitrary(MyStackMachine.class);
	}

}
