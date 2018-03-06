package pbt.jmarticle;

import net.jqwik.api.*;

class FixedSeeds {

	@Property(seed = "424242", reporting = Reporting.GENERATED)
	void alwaysTheSameValues(@ForAll int aNumber) {

	}


	@Property(reporting = Reporting.GENERATED)
	void neverTheSameValues(@ForAll int aNumber) {

	}
}
