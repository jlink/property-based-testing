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

	@Property
	void numbers_below_2_are_illegal(
			@ForAll @IntRange(min = Integer.MIN_VALUE, max = 1) int number
	) {
		Assertions.assertThatThrownBy(() -> {
			Primes.factorize(number);
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void all_numbers_above_1_can_be_factorized(
			@ForAll @IntRange(min = 2) int number
	) {
		number = Integer.MAX_VALUE;
		List<Integer> factors = Primes.factorize(number);
		Integer product = factors.stream().reduce(1, (a, b) -> a * b);
		Assertions.assertThat(product).isEqualTo(number);
	}

	@Provide
	Arbitrary<List<Integer>> listOfPrimes() {
		return primes().list().ofMinSize(1).ofMaxSize(20);
	}

	@Provide
	Arbitrary<Integer> primes() {
		return Arbitraries.of(
				2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47,
				53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101
		);
	}

}
