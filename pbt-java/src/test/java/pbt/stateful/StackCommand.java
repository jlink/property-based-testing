package pbt.stateful;

import org.assertj.core.api.Assertions;

abstract public class StackCommand {
	public static PushCommand push(String aString) {
		return new PushCommand(aString);
	}

	public static PopCommand pop() {
		return new PopCommand();
	}

	public static ClearCommand clear() {
		return new ClearCommand();
	}


	abstract void execute(StackModel model);

	boolean precondition(StackModel model) {
		return true;
	}

	void postcondition(StackModel model) {

	}

	private static class PushCommand extends StackCommand {

		private final String aString;

		PushCommand(String aString) {
			this.aString = aString;
		}

		@Override
		public String toString() {
			return String.format("Push[%s]", aString);
		}

		@Override
		void execute(StackModel model) {
			model.stack.push(aString);
		}

		@Override
		public void postcondition(StackModel model) {
			Assertions.assertThat(model.stack.isEmpty()).isFalse();
		}
	}

	private static class PopCommand extends StackCommand {
		@Override
		public String toString() {
			return "Pop";
		}

		@Override
		boolean precondition(StackModel model) {
			return !model.stack.isEmpty();
		}

		@Override
		void execute(StackModel model) {
			model.stack.pop();
		}
	}

	private static class ClearCommand extends StackCommand {
		@Override
		public String toString() {
			return "Clear";
		}

		@Override
		void execute(StackModel model) {
			model.stack.clear();
		}
	}
}
