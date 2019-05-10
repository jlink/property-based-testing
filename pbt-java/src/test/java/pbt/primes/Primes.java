package pbt.primes;

import java.util.*;

public class Primes {
	public static List<Integer> factorize(int number) {
		List<Integer> factors = new ArrayList<>();
		int candidate = 2;
		while (number >= candidate) {
			while (number % candidate != 0) {
				candidate++;
			}
			factors.add(candidate);
			number /= candidate;
		}
		return factors;
	}
}
