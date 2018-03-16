package pbt.stateful.stack;

import java.util.List;

public class StackModel {

	MyStringStack stack = new MyStringStack();

	public void executeCommands(List<StackCommand> commands) {
		for (StackCommand command : commands) {
			if (command.precondition(this)) {
				command.execute(this);
			}
		}
	}

}
