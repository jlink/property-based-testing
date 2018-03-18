package pbt.stateful;

import java.util.List;

import net.jqwik.api.Shrinkable;

public interface StateMachineRunner<S extends StateMachine> {
	List<Shrinkable<Action>> runSequence();

	boolean hasRun();

	@SuppressWarnings("unchecked")
	void run();
}
