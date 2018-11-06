package pbt.oracle.javamagazine;

import net.jqwik.api.*;

class ZipCodeProperties {

	@Property
	@Report(Reporting.GENERATED)
	void letsGenerateZipCodes(@ForAll("germanZipCode") String zipCode) { }

	@Provide
	Arbitrary<String> germanZipCode() {
		return Arbitraries.strings()
						  .withCharRange('0', '9')
						  .ofLength(5)
						  .filter(z -> !z.startsWith("00"));
	}

}
