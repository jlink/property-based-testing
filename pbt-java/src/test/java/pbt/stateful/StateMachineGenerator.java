package pbt.stateful;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.stream.*;

public class StateMachineGenerator<S extends StateMachine> implements RandomGenerator<StateMachineRunner<S>> {
	private final S stateMachine;
	private final int genSize;
	private final int numberOfActions;

	public StateMachineGenerator(S stateMachine, int genSize, int numberOfActions) {
		this.stateMachine = stateMachine;
		this.genSize = genSize;
		this.numberOfActions = numberOfActions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Shrinkable<StateMachineRunner<S>> next(Random random) {
		List<Arbitrary<Action>> arbitraries = stateMachine.actions();
		List<RandomGenerator<Action>> generators = arbitraries.stream().map(arbitrary -> arbitrary.generator(genSize)).collect(Collectors.toList());
		List<Shrinkable<Action>> candidateActions = generateCandidates(generators, numberOfActions, random);
		return new StateMachineRunnerShrinkable(stateMachine, candidateActions);
	}

	private List<Shrinkable<Action>> generateCandidates(List<RandomGenerator<Action>> generators, int numberOfActions, Random random) {
		List<Shrinkable<Action>> candidates = new ArrayList<>();
		for (int i = 0; i < numberOfActions; i++) {
			Shrinkable<Action> next = RandomGenerators
					.chooseValue(generators, random)
					.sampleRandomly(random);
			candidates.add(next);
		}
		return candidates;
	}

}
