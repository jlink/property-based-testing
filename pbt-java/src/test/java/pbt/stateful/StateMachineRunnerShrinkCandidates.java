package pbt.stateful;

import java.util.*;
import java.util.stream.Collectors;

import net.jqwik.api.Shrinkable;
import net.jqwik.properties.arbitraries.ShrinkCandidates;

public class StateMachineRunnerShrinkCandidates<S extends StateMachine> implements ShrinkCandidates<StateMachineRunner<S>> {

	@Override
	public Set<StateMachineRunner<S>> nextCandidates(StateMachineRunner<S> value) {
		Set<StateMachineRunner<S>> shrunkSequence = shrinkSequence(value);
		// TODO: Shrink actions if sequence cannot be shrunk any more
		return shrunkSequence;
	}

	private Set<StateMachineRunner<S>> shrinkSequence(StateMachineRunner<S> stateMachineRunner) {
		SequentialStateMachineRunner<S> sequentialRunner = (SequentialStateMachineRunner<S>) stateMachineRunner;

		Set<List<Shrinkable<Action>>> setOfSequences = shrinkActions(sequentialRunner.runSequence());
		return setOfSequences.stream()
				.map(seq -> (StateMachineRunner<S>) new SequentialStateMachineRunner<>(sequentialRunner.getStateMachine(), seq))
				.collect(Collectors.toSet());
	}

	private Set<List<Shrinkable<Action>>> shrinkActions(List<Shrinkable<Action>> sequence) {
		if (sequence.size() <= 1) {
			return Collections.emptySet();
		}
		Set<List<Shrinkable<Action>>> setOfSequences = new HashSet<>();
		for (int i = 0; i < sequence.size(); i++) {
			ArrayList<Shrinkable<Action>> newCandidate = new ArrayList<>(sequence);
			newCandidate.remove(i);
			setOfSequences.add(newCandidate);
		}
		return setOfSequences;
	}

	@Override
	public int distance(StateMachineRunner<S> stateMachineRunner) {
		return stateMachineRunner.runSequence().stream().mapToInt(Shrinkable::distance).sum();
	}
}
