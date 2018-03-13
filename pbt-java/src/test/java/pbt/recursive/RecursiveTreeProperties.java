package pbt.recursive;

import net.jqwik.api.*;

class RecursiveTreeProperties {

	@Property(tries = 10, reporting = Reporting.GENERATED)
	boolean trees(@ForAll Tree aTree) {
		return aTree.countLeaves() >= 1;
	}

	@Provide
	// Recursive arbitraries currently do not work
	Arbitrary<Tree> trees() {
		// will throw StackOverflowError
		Arbitrary<Tree> left = trees();
		Arbitrary<Tree> right = trees();
		Arbitrary<Tree> branch = Combinators.combine(left, right).as(Branch::new);
		return Arbitraries.oneOf(
				Arbitraries.constant(new Leaf()), branch
		);
	}

}
