package pbt.jmarticle;

import net.jqwik.api.*;

class FixedSeeds {

	@Property(seed = 424242l, reporting = ReportingMode.GENERATED)
	void alwaysTheSameValues(@ForAll int aNumber) {

	}


	@Property(reporting = ReportingMode.GENERATED)
	void neverTheSameValues(@ForAll int aNumber) {

	}
}
