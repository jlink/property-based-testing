package pbt.stateful;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;
import org.junit.platform.commons.support.*;

import java.util.*;
import java.util.stream.*;

public class StateMachineGenerator<S extends StateMachine> implements RandomGenerator<S> {
	private final Class<S> stateMachineClass;
	private final int genSize;
	private final int numberOfActions;

	public StateMachineGenerator(Class<S> stateMachineClass, int genSize, int numberOfActions) {
		this.stateMachineClass = stateMachineClass;
		this.genSize = genSize;
		this.numberOfActions = numberOfActions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Shrinkable<S> next(Random random) {
		S stateMachine = ReflectionSupport.newInstance(stateMachineClass);
		List<Arbitrary<Action>> arbitraries = stateMachine.actions();
		List<RandomGenerator<Action>> generators = arbitraries.stream()
				.map(arbitrary -> arbitrary.generator(genSize))
				.collect(Collectors.toList());
		fillActionCandidates(stateMachine, random, generators, numberOfActions);
		return Shrinkable.unshrinkable(stateMachine);
	}

	private void fillActionCandidates(S stateMachine, Random random, List<RandomGenerator<Action>> generators, int numberOfActions) {

		for (int i = 0; i < numberOfActions; i++) {
			Shrinkable<Action> next = RandomGenerators
					.chooseValue(generators, random)
					.sampleRandomly(random);

			//noinspection unchecked
			stateMachine.addCandidate(next);
		}

	}

}
