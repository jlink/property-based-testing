package pbt.fizzbuzz;

import net.jqwik.api.*;

class FizzBuzzProperties {

	@Property
	boolean failForAStart() {
		return false;
	}

}
