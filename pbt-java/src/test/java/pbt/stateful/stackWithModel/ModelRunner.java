package pbt.stateful.stackWithModel;

import pbt.stateful.stackWithModel.Model.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.stateful.*;

public class ModelRunner<S, M> {
	private Model<S, M> model;

	public ModelRunner(Model<S, M> model) {
		this.model = model;
	}

	public Arbitrary<ActionSequence<Tuple2<S, M>>> sequences() {
		Arbitrary<ModelAction<S, M>> modelActions = model.actions();
		Arbitrary<Action<Tuple2<S, M>>> actions = modelActions.map(modelAction -> new Action<Tuple2<S, M>>() {

			@Override
			public boolean precondition(Tuple2<S, M> model) {
				return modelAction.precondition(model.get2());
			}

			@Override
			public Tuple2<S, M> run(Tuple2<S, M> pair) {
				S nextState = modelAction.runOnState(pair.get1());
				M nextModelState = modelAction.runOnModel(pair.get2());
				modelAction.assertPostcondition();
				model.assertState(nextState, nextModelState);
				return Tuple.of(nextState, nextModelState);
			}

			@Override
			public String toString() {
				return modelAction.toString();
			}
		});
		return Arbitraries.sequences(actions);
	}
}
