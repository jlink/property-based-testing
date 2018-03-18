package pbt.stateful;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.function.*;

public class StateMachineRunnerShrinkable<S extends StateMachine> implements Shrinkable<StateMachineRunner<S>> {

	private final S stateMachine;
	private final List<Shrinkable<Action>> sequence;
	private final StateMachineRunner<S> value;

	public StateMachineRunnerShrinkable(S stateMachine, List<Shrinkable<Action>> sequence) {
		this.stateMachine = stateMachine;
		this.sequence = sequence;
		this.value = new StateMachineRunner<>(stateMachine, sequence);
	}

	@Override
	public Set<ShrinkResult<Shrinkable<StateMachineRunner<S>>>> shrinkNext(Predicate<StateMachineRunner<S>> falsifier) {
		// TODO: First shrink sequence then actions
		// see ContainerShrinkable.shrinkNext
		return Collections.emptySet();
	}

	@Override
	public StateMachineRunner<S> value() {
		return value;
	}

	@Override
	public int distance() {
		return 0;
	}
}
