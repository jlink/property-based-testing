package pbt.stateful.stack;

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
			int sizeBefore = model.stack.size();
			model.stack.push(aString);
			Assertions.assertThat(model.stack.isEmpty()).isFalse();
			Assertions.assertThat(model.stack.size()).isEqualTo(sizeBefore + 1);
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
			int sizeBefore = model.stack.size();
			String topBefore = model.stack.top();

			String popped = model.stack.pop();
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
			Assertions.assertThat(model.stack.isEmpty());
		}
	}
}
