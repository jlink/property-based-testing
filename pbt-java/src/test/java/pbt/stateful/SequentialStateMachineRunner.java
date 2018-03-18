package pbt.stateful;

import java.util.*;
import java.util.stream.Collectors;

import net.jqwik.JqwikException;
import net.jqwik.api.*;
import net.jqwik.support.*;
import org.opentest4j.AssertionFailedError;

public class SequentialStateMachineRunner<S extends StateMachine> implements StateMachineRunner<S> {

	public static <S extends StateMachine> Arbitrary<StateMachineRunner<S>> arbitrary(Class<S> stateMachineClass) {
		int numberOfActions = 10;
		S stateMachine = JqwikReflectionSupport.newInstanceWithDefaultConstructor(stateMachineClass);
		return genSize -> new StateMachineGenerator<>(stateMachine, genSize, numberOfActions);
	}


	private final S stateMachine;
	private final List<Shrinkable<Action>> candidateSequence;
	private final List<Shrinkable<Action>> runSequence = new ArrayList<>();

	private boolean hasRun = false;

	public SequentialStateMachineRunner(S stateMachine, List<Shrinkable<Action>> candidateSequence) {
		this.stateMachine = stateMachine;
		this.candidateSequence = candidateSequence;
	}

	public S getStateMachine() {
		return stateMachine;
	}

	@Override
	public List<Shrinkable<Action>> runSequence() {
		if (!hasRun) {
			throw new JqwikException(String.format("State machine %s has not run yet.", stateMachine));
		}
		return runSequence;
	}

	@Override
	public boolean hasRun() {
		return hasRun;
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized void run() {
		if (hasRun) {
			runSequence.clear();
		}

		Object model = stateMachine.createModel();
		runSequence.clear();
		hasRun = true;
		try {
			for (Shrinkable<Action> candidate : candidateSequence) {
				Action action = candidate.value();
				if (action.precondition(model)) {
					runSequence.add(candidate);
					action.run(model);
				}
			}
		} catch (Throwable t) {
			String actionsString = runSequence.stream() //
					.map(actionShrinkable -> "   " + actionShrinkable.value().toString()) //
					.collect(Collectors.joining(System.lineSeparator()));
			String message = String.format("State machine [%s] failed with following actions:%s%s", stateMachine.getClass().getSimpleName(), System.lineSeparator(), actionsString);
			throw new AssertionFailedError(message, t);
		}
	}

	@Override
	public String toString() {
		String stateString = "";
		List<Shrinkable<Action>> actionsToShow = runSequence;
		if (!hasRun) {
			stateString = "(not yet run)";
			actionsToShow = candidateSequence;
		}
		String actionsString = JqwikStringSupport.displayString(extractValues(actionsToShow));
		return String.format("%s%s:%s", this.getClass().getSimpleName(), stateString, actionsString);
	}

	private List<Action> extractValues(List<Shrinkable<Action>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}
}
