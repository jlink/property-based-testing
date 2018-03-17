package pbt.stateful;

import net.jqwik.api.*;
import net.jqwik.support.*;

import java.util.*;
import java.util.stream.*;

public abstract class StateMachine<M> {

	private List<Shrinkable<Action<M>>> candidateActions = new ArrayList<>();

	private List<Action<M>> runActions = new ArrayList<>();

	private boolean hasRun = false;

	protected abstract List<Arbitrary<Action<M>>> actions();

	protected abstract M createModel();

	void addCandidate(Shrinkable<Action<M>> shrinkable) {
		candidateActions.add(shrinkable);
	}

	public void run() {
		M model = createModel();
		runActions.clear();
		hasRun = true;
		for (Shrinkable<Action<M>> candidate : candidateActions) {
			Action<M> action = candidate.value();
			if (action.precondition(model)) {
				runActions.add(action);
				action.run(model);
			}
		}
	}

	public static <S extends StateMachine> Arbitrary<S> arbitrary(Class<S> stateMachineClass) {
		int numberOfActions = 10;
		return genSize -> new StateMachineGenerator<>(stateMachineClass, genSize, numberOfActions);
	}

	@Override
	public String toString() {
		String stateString = "unrun";
		String actionsString = "";
		if (hasRun) {
			stateString = "run";
			actionsString = JqwikStringSupport.displayString(runActions);
		} else {
			List<Action<M>> candidates = candidateActions.stream().map(shrinkable -> shrinkable.value()).collect(Collectors.toList());
			actionsString = JqwikStringSupport.displayString(candidates);
		}
		return String.format("%s(%s):%s", this.getClass().getSimpleName(), stateString, actionsString);
	}
}
