package pbt.recursive;

import net.jqwik.api.*;

import java.util.function.*;

class RecursiveTreeProperties {

	@Property(tries = 10, reporting = Reporting.GENERATED)
	boolean randomTrees(@ForAll("trees") Tree aTree) {
		return aTree.countLeaves() == countLeaves(aTree);
	}

	@Provide
	Arbitrary<Tree> trees() {
		Arbitrary<Tree> trees = Arbitraries.lazy(() -> trees());
		Arbitrary<Tree> branch = Combinators.combine(trees, trees).as(Branch::new);
		Arbitrary<Tree> leaf = Arbitraries.strings() //
				.ofLength(5) //
				.withCharRange('A', 'F') //
				.map(Leaf::new);

		// Probability of leaf must be higher than of branch. Otherwise a stack overflow is likely.
		return Arbitraries.frequency( //
				Tuples.tuple(3, leaf), //
				Tuples.tuple(2, branch) //
		).flatMap(Function.identity());
	}

	private int countLeaves(Tree aTree) {
		if (aTree instanceof Leaf)
			return 1;
		Branch branch = (Branch) aTree;
		return countLeaves(branch.left()) + countLeaves(branch.right());
	}

}
