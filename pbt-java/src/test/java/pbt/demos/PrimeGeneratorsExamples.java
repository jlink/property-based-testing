package pbt.demos;

import net.jqwik.api.*;

import java.util.*;

class PrimeGeneratorsExamples {

	@Property
	boolean primes_cannot_be_factored(@ForAll("primes") int aPrime) {
		return factor(aPrime).equals(Collections.singletonList(aPrime));
	}

	@Property(reporting = Reporting.GENERATED)
	boolean primes_cannot_be_factored_2(@ForAll("primesGenerated") int aPrime) {
		return factor(aPrime).equals(Collections.singletonList(aPrime));
	}

	@Provide
	Arbitrary<Integer> primes() {
		return Arbitraries.of(2, 3, 5, 7, 11, 13, 17, 19);
	}

	@Provide
	Arbitrary<Integer> primesGenerated() {
		return Arbitraries.randomValue(random -> generatePrime(random));
	}

	private Integer generatePrime(Random random) {
		int candidate;
		do {
			candidate = random.nextInt(10000) + 2;
		} while (!isPrime(candidate));
		return candidate;
	}

	static List<Integer> factor(int aNumber) {
		List<Integer> factors = new ArrayList<>();
		int rest = aNumber;
		for (int factorCandidate = 2; factorCandidate <= aNumber / 2; factorCandidate++) {
			while (rest % factorCandidate == 0) {
				factors.add(factorCandidate);
				rest = rest / factorCandidate;
			}
		}
		if (rest != 1) factors.add(rest);
		return factors;
	}

	private static boolean isPrime(int candidate) {
		if (candidate <= 3 || candidate % 2 == 0) return candidate == 2 || candidate == 3;
		int divisor = 3;
		while ((divisor <= Math.sqrt(candidate)) && (candidate % divisor != 0)) divisor += 2;
		return candidate % divisor != 0;
	}

}
