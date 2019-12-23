package pbt.stateful.stackWithModel;

import net.jqwik.api.*;

public interface Model<S, M> {

	void assertState(S state, M modelState);

	Arbitrary<ModelAction<S, M>> actions();

	interface ModelAction<S, M> {
		default boolean preconditionOnModel(M modelState) {
			return true;
		}

		default boolean preconditionOnState(S state) {
			// Must be kept in sync with preconditionOnModel()
			return true;
		}

		S runOnState(S state);

		M runOnModel(M modelState);

		default void assertPostcondition() {
		}
	}

}
