package pbt.stateful.stack;

import java.io.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.Action;
import org.assertj.core.api.Assertions;

class MyStringStackActions {


	static Arbitrary<Action<MyStringStack>> actions() {
		return Arbitraries.oneOf(push(), clear(), pop());
	}

	static Arbitrary<Action<MyStringStack>> push() {
		return Arbitraries.strings().alpha().ofLength(5).map(PushAction::new);
	}

	private static Arbitrary<Action<MyStringStack>> clear() {
		return Arbitraries.just(new ClearAction());
	}

	private static Arbitrary<Action<MyStringStack>> pop() {
		return Arbitraries.just(new PopAction());
	}

	private static class PushAction implements Action<MyStringStack>, Serializable {

		private final String element;

		private PushAction(String element) {
			this.element = element;
		}

		@Override
		public MyStringStack run(MyStringStack stack) {
			int sizeBefore = stack.size();
			stack.push(element);
			Assertions.assertThat(stack.isEmpty()).isFalse();
			Assertions.assertThat(stack.size()).isEqualTo(sizeBefore + 1);
			Assertions.assertThat(stack.top()).isEqualTo(element);
			return stack;
		}

		@Override
		public String toString() {
			return String.format("push(%s)", element);
		}
	}

	private static class ClearAction implements Action<MyStringStack>, Serializable {

		@Override
		public MyStringStack run(MyStringStack stack) {
			stack.clear();
			Assertions.assertThat(stack.isEmpty()).isTrue();
			return stack;
		}

		@Override
		public String toString() {
			return "clear";
		}
	}

	private static class PopAction implements Action<MyStringStack>, Serializable {

		@Override
		public boolean precondition(MyStringStack stack) {
			return !stack.isEmpty();
		}

		@Override
		public MyStringStack run(MyStringStack stack) {
			int sizeBefore = stack.size();
			String topBefore = stack.top();

			String popped = stack.pop();
			Assertions.assertThat(popped).isEqualTo(topBefore);
			Assertions.assertThat(stack.size()).isEqualTo(sizeBefore - 1);

			return stack;
		}

		@Override
		public String toString() {
			return "pop";
		}
	}
}
