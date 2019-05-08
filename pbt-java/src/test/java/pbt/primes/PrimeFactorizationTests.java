package pbt.primes;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class PrimeFactorizationTests {
	@Example
	void factorizing_2_returns_list_with_just_2() {
		List<Integer> factors = Primes.factorize(2);
		Assertions.assertThat(factors).containsExactly(2);
	}
}
