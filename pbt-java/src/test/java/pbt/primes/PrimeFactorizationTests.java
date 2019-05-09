package pbt.primes;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class PrimeFactorizationTests {

	@Example
	void factorizing_2_returns_list_with_just_2() {
		List<Integer> factors = Primes.factorize(2);
		Assertions.assertThat(factors).containsExactly(2);
	}

	@Property
	void factorizing_a_prime_returns_list_with_just_the_prime(@ForAll("primes") int prime) {
		List<Integer> factors = Primes.factorize(prime);
		Assertions.assertThat(factors).containsExactly(prime);
	}

	@Property
	void factorizing_prime_raised_to_n_returns_n_times_prime(
			@ForAll("primes") int prime,
			@ForAll @IntRange(min = 1, max = 5) int n
	) {
		Assume.that(BigInteger.valueOf(prime).pow(n)
							  .compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0);
		List<Integer> factors = Primes.factorize((int) Math.pow(prime, n));
		Assertions.assertThat(factors).containsOnly(prime);
		Assertions.assertThat(factors).hasSize(n);
	}

	@Provide
	Arbitrary<Integer> primes() {
		return Arbitraries.of(2, 3, 5, 7, 23, 101);
	}

}
