package pbt.stateful;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.support.*;

import java.util.*;
import java.util.stream.*;

public class StateMachineRunner<S extends StateMachine> {

	public static <S extends StateMachine> Arbitrary<StateMachineRunner<S>> arbitrary(Class<S> stateMachineClass) {
		int numberOfActions = 10;
		S stateMachine = JqwikReflectionSupport.newInstanceWithDefaultConstructor(stateMachineClass);
		return genSize -> new StateMachineGenerator<>(stateMachine, genSize, numberOfActions);
	}


	private final S stateMachine;
	private final List<Shrinkable<Action>> candidateActions;

	private final List<Shrinkable<Action>> runActions = new ArrayList<>();

	private boolean hasRun = false;

	public StateMachineRunner(S stateMachine, List<Shrinkable<Action>> candidateActions) {
		this.stateMachine = stateMachine;
		this.candidateActions = candidateActions;
	}

	public List<Shrinkable<Action>> sequence() {
		if (!hasRun) {
			throw new JqwikException(String.format("State machine %s has not run yet.", stateMachine));
		}
		return runActions;
	}

	public boolean hasRun() {
		return hasRun;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		if (hasRun) {
			throw new JqwikException(String.format("State machine %s has already run.", stateMachine));
		}

		Object model = stateMachine.createModel();
		runActions.clear();
		hasRun = true;
		for (Shrinkable<Action> candidate : candidateActions) {
			Action action = candidate.value();
			if (action.precondition(model)) {
				runActions.add(candidate);
				action.run(model);
			}
		}
	}

	@Override
	public String toString() {
		String stateString = "";
		List<Shrinkable<Action>> actionsToShow = runActions;
		if (!hasRun) {
			stateString = "(not yet run)";
			actionsToShow = candidateActions;
		}
		String actionsString = JqwikStringSupport.displayString(extractValues(actionsToShow));
		return String.format("%s%s:%s", this.getClass().getSimpleName(), stateString, actionsString);
	}

	private List<Action> extractValues(List<Shrinkable<Action>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}
}
