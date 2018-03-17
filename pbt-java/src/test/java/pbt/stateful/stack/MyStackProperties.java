package pbt.stateful.stack;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import pbt.stateful.*;

import java.util.*;

class MyStackProperties {

	@Property(tries = 10, reporting = Reporting.GENERATED)
	void checkMyStackMachine(@ForAll MyStackMachine stackMachine) {
		stackMachine.run();
	}

	@Provide
	Arbitrary<MyStackMachine> stackMachine() {
		return StateMachine.arbitrary(MyStackMachine.class);
	}

	@Property(reporting = Reporting.GENERATED)
	void runCommands(@ForAll @Size(min= 1, max = 20) List<StackCommand> commands) {
		StackModel model = new StackModel();
		model.executeCommands(commands);
	}

	@Provide
	Arbitrary<List<StackCommand>> commands() {
		return Arbitraries.oneOf(push(), pop(), clear()).list();
	}

	private Arbitrary<StackCommand> push() {
		return Arbitraries.strings().alpha().ofLength(5).map(StackCommand::push);
	}
	private Arbitrary<StackCommand> pop() {
		return Arbitraries.constant(StackCommand.pop());
	}
	private Arbitrary<StackCommand> clear() {
		return Arbitraries.constant(StackCommand.clear());
	}
}
