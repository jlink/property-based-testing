package pbt.stateful;

public interface Action<M> {

	default boolean precondition(M model) {
		return true;
	}

	void run(M model);
}
