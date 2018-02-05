package pbt.jmarticle;

import net.jqwik.api.*;

class GeneratingValuesProperties {

	@Property(reporting = Reporting.GENERATED)
	void letsGeneratePostleitzahlen(@ForAll("postleitzahlen") String zipCode) {
	}

	@Provide
	Arbitrary<String> postleitzahlen() {
		return Arbitraries.strings('0', '9', 5, 5);
	}
}
