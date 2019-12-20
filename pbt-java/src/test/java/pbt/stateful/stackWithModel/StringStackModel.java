package pbt.stateful.stackWithModel;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

public class StringStackModel implements Model<StringStack, List<String>> {

	@Override
	public void assertState(StringStack stack, List<String> list) {
		Assertions.assertThat(stack.size()).isEqualTo(list.size());
		Assertions.assertThat(stack.isEmpty()).isEqualTo(list.isEmpty());
	}

	@Override
	public Arbitrary<ModelAction<StringStack, List<String>>> actions() {
		Arbitrary<ModelAction<StringStack, List<String>>> push =
				Arbitraries.strings()
						   .alpha().ofLength(5)
						   .map(PushAction::new);
		Arbitrary<ModelAction<StringStack, List<String>>> clear = Arbitraries.constant(new ClearAction());
		Arbitrary<ModelAction<StringStack, List<String>>> pop = Arbitraries.constant(new PopAction());
		return Arbitraries.oneOf(push, clear, pop);
	}

	private static class PushAction implements ModelAction<StringStack, List<String>> {

		private final String element;

		private PushAction(String element) {
			this.element = element;
		}

		@Override
		public String toString() {
			return String.format("push(%s)", element);
		}

		@Override
		public StringStack runOnState(StringStack stack) {
			stack.push(element);
			return stack;
		}

		@Override
		public List<String> runOnModel(List<String> list) {
			list.add(element);
			return list;
		}
	}

	private static class ClearAction implements ModelAction<StringStack, List<String>> {

		@Override
		public StringStack runOnState(StringStack stack) {
			stack.clear();
			return stack;
		}

		@Override
		public List<String> runOnModel(List<String> list) {
			list.clear();
			return list;
		}

		@Override
		public String toString() {
			return "clear";
		}

	}

	private static class PopAction implements ModelAction<StringStack, List<String>> {

		private String poppedFromStack;
		private String poppedFromModel;

		@Override
		public boolean precondition(List<String> list) {
			return !list.isEmpty();
		}

		@Override
		public StringStack runOnState(StringStack stack) {
			poppedFromStack = stack.pop();
			return stack;
		}

		@Override
		public List<String> runOnModel(List<String> list) {
			poppedFromModel = list.get(list.size() - 1);
			return list;
		}

		@Override
		public void assertPostcondition() {
			Assertions.assertThat(poppedFromStack).isEqualTo(poppedFromModel);
		}

		@Override
		public String toString() {
			return "pop";
		}

	}

}
