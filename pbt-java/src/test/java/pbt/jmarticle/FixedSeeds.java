package pbt.jmarticle;

import net.jqwik.api.*;

class FixedSeeds {

	@Property(seed = "424242")
	@Report(Reporting.GENERATED)
	void alwaysTheSameValues(@ForAll int aNumber) {

	}


	@Property @Report(Reporting.GENERATED)
	void neverTheSameValues(@ForAll int aNumber) {

	}
}
