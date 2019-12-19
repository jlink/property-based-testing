package pbt.stateful.stackWithModel;

import java.io.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

public class StackModelSpecification {

	private MyStringStack2 stack;

	private List<String> model = new ArrayList<>();

	public StackModelSpecification(MyStringStack2 stack) {
		this.stack = stack;
	}

	public Arbitrary<Action<MyStringStack2>> actions() {
		// TODO
		return null;
	}

	private class PushAction implements Action<MyStringStack2>, Serializable {

		private final String element;

		private PushAction(String element) {
			this.element = element;
		}

		@Override
		public MyStringStack2 run(MyStringStack2 stack) {
			model.add(element);
			stack.push(element);
			Assertions.assertThat(stack.size()).isEqualTo(model.size());
			return stack;
		}

		@Override
		public String toString() {
			return String.format("push(%s)", element);
		}
	}

}
