package pbt.stateful;

import org.assertj.core.api.*;

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
		private int sizeBefore;

		PushCommand(String aString) {
			this.aString = aString;
		}

		@Override
		public String toString() {
			return String.format("Push[%s]", aString);
		}

		@Override
		void execute(StackModel model) {
			sizeBefore = model.stack.size();

			model.stack.push(aString);
		}

		@Override
		public void postcondition(StackModel model) {
			Assertions.assertThat(model.stack.isEmpty()).isFalse();
			Assertions.assertThat(model.stack.size()).isEqualTo(sizeBefore + 1);
		}
	}

	private static class PopCommand extends StackCommand {
		private String popped;
		private int sizeBefore;
		private String topBefore;

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
			sizeBefore = model.stack.size();
			topBefore = model.stack.top();

			popped = model.stack.pop();
		}

		@Override
		void postcondition(StackModel model) {
			Assertions.assertThat(popped).isEqualTo(topBefore);
			Assertions.assertThat(model.stack.size()).isEqualTo(sizeBefore - 1);
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

		@Override
		void postcondition(StackModel model) {
			Assertions.assertThat(model.stack.isEmpty());
		}
	}
}
