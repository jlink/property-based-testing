package pbt.primes;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
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
		BigInteger numberToFactorize = BigInteger.valueOf(prime).pow(n);
		Assume.that(numberToFactorize.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0);
		List<Integer> factors = Primes.factorize(numberToFactorize.intValueExact());
		Assertions.assertThat(factors).containsOnly(prime);
		Assertions.assertThat(factors).hasSize(n);
	}

	@Property
	void factorizing_product_of_list_of_primes_will_return_original_list(
			@ForAll("listOfPrimes") List<Integer> primes
	) {
		primes.sort(Integer::compareTo);
		BigInteger product =
				primes.stream()
					  .map(BigInteger::valueOf)
					  .reduce(BigInteger.ONE, BigInteger::multiply);
		Assume.that(product.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0);

		List<Integer> factors = Primes.factorize(product.intValueExact());
		Assertions.assertThat(factors).isEqualTo(primes);
	}

	@Provide
	Arbitrary<List<Integer>> listOfPrimes() {
		return primes().list().ofMinSize(1).ofMaxSize(5);
	}

	@Provide
	Arbitrary<Integer> primes() {
		return Arbitraries.of(2, 3, 5, 7, 23, 101);
	}

}
