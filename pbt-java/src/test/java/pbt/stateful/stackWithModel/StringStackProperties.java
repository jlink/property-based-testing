package pbt.stateful.stackWithModel;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.stateful.*;

class StringStackProperties {

	@Property(tries = 100)
	@Report(Reporting.GENERATED)
	void checkMyStackMachine(@ForAll("sequences") ActionSequence<Tuple2<StringStack, List<String>>> sequence) {
		Tuple2<StringStack, List<String>> initial = Tuple.of(new StringStack(), new ArrayList<>());
		sequence.run(initial);
	}

	@Provide
	Arbitrary<ActionSequence<Tuple2<StringStack, List<String>>>> sequences() {
		StringStackModel model = new StringStackModel();
		ModelRunner<StringStack, List<String>> modelModelRunner = new ModelRunner<>(model);
		return modelModelRunner.sequences();
	}

}
