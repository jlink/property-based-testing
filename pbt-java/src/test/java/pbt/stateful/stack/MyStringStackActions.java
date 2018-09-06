package pbt.stateful.stack;

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
		return Arbitraries.constant(new ClearAction());
	}

	private static Arbitrary<Action<MyStringStack>> pop() {
		return Arbitraries.constant(new PopAction());
	}

	private static class PushAction implements Action<MyStringStack> {

		private final String element;

		private PushAction(String element) {
			this.element = element;
		}

		@Override
		public MyStringStack run(MyStringStack model) {
			int sizeBefore = model.size();
			model.push(element);
			Assertions.assertThat(model.isEmpty()).isFalse();
			Assertions.assertThat(model.size()).isEqualTo(sizeBefore + 1);
			Assertions.assertThat(model.top()).isEqualTo(element);
			return model;
		}

		@Override
		public String toString() {
			return String.format("push(%s)", element);
		}
	}

	private static class ClearAction implements Action<MyStringStack> {

		@Override
		public MyStringStack run(MyStringStack model) {
			model.clear();
			Assertions.assertThat(model.isEmpty()).isTrue();
			return model;
		}

		@Override
		public String toString() {
			return "clear";
		}
	}

	private static class PopAction implements Action<MyStringStack> {

		@Override
		public boolean precondition(MyStringStack model) {
			return !model.isEmpty();
		}

		@Override
		public MyStringStack run(MyStringStack model) {
			int sizeBefore = model.size();
			String topBefore = model.top();

			String popped = model.pop();
			Assertions.assertThat(popped).isEqualTo(topBefore);
			Assertions.assertThat(model.size()).isEqualTo(sizeBefore - 1);

			return model;
		}

		@Override
		public String toString() {
			return "pop";
		}
	}
}
