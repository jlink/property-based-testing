package pbt.stateful;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;

class MyStackProperties {

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
