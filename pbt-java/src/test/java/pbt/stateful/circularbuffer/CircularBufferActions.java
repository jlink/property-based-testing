package pbt.stateful.circularbuffer;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class CircularBufferActions {

	static Arbitrary<Action<CircularBuffer>> actions() {
		return Arbitraries.oneOf(create(), put(), get(), size());
	}

	static Arbitrary<Action<CircularBuffer>> create() {
		return Arbitraries.integers().between(0, 100).map(NewAction::new);
	}

	static Arbitrary<Action<CircularBuffer>> put() {
		return Arbitraries.integers().map(Object::toString).map(PutAction::new);
	}

	private static Arbitrary<Action<CircularBuffer>> get() {
		return Arbitraries.constant(new GetAction());
	}

	private static Arbitrary<Action<CircularBuffer>> size() {
		return Arbitraries.constant(new SizeAction());
	}

	private static class NewAction implements Action<CircularBuffer> {

		private final int initialCapacity;

		public NewAction(int initialCapacity) {
			this.initialCapacity = initialCapacity;
		}

		@Override
		public CircularBuffer run(CircularBuffer model) {
			return new CircularBuffer(initialCapacity);
		}

		@Override
		public String toString() {
			return String.format("new CircularBuffer(%s)", initialCapacity);
		}

	}

	private static class PutAction implements Action<CircularBuffer> {

		private final Object element;

		public PutAction(Object element) {
			this.element = element;
		}

		@Override
		public boolean precondition(CircularBuffer model) {
			return model != null;
		}

		@Override
		public CircularBuffer run(CircularBuffer model) {
			return model;
		}

		@Override
		public String toString() {
			return String.format("put(%s)", element);
		}

	}

	private static class GetAction implements Action<CircularBuffer> {

		@Override
		public boolean precondition(CircularBuffer model) {
			return model != null;
		}

		@Override
		public CircularBuffer run(CircularBuffer model) {
			return model;
		}
		@Override
		public String toString() {
			return "get()";
		}
	}

	private static class SizeAction implements Action<CircularBuffer> {

		@Override
		public boolean precondition(CircularBuffer model) {
			return model != null;
		}

		@Override
		public CircularBuffer run(CircularBuffer model) {
			return model;
		}

		@Override
		public String toString() {
			return "size()";
		}
	}
}
