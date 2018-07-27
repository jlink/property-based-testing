package pbt.stateful.circularbuffer;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class CircularBufferActions {

	static Arbitrary<Action<CircularBuffer>> actions() {
		return Arbitraries.oneOf(put(), get(), size());
	}

	static Arbitrary<Action<CircularBuffer>> put() {
		return null;
//		return Arbitraries.strings().alpha().ofLength(5).map(PutAction::new);
	}

	private static Arbitrary<Action<CircularBuffer>> get() {
		return null;
//		return Arbitraries.constant(new GetAction());
	}

	private static Arbitrary<Action<CircularBuffer>> size() {
		return null;
//		return Arbitraries.constant(new SizeAction());
	}

	private static class PutAction implements Action<CircularBuffer> {

		@Override
		public boolean precondition(CircularBuffer model) {
			return false;
		}

		@Override
		public CircularBuffer run(CircularBuffer model) {
			return null;
		}
	}

	private static class GetAction implements Action<CircularBuffer> {

		@Override
		public boolean precondition(CircularBuffer model) {
			return false;
		}

		@Override
		public CircularBuffer run(CircularBuffer model) {
			return null;
		}
	}

	private static class SizeAction implements Action<CircularBuffer> {

		@Override
		public boolean precondition(CircularBuffer model) {
			return false;
		}

		@Override
		public CircularBuffer run(CircularBuffer model) {
			return null;
		}
	}
}
