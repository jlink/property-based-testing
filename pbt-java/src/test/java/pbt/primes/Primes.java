package pbt.primes;

import java.util.*;

public class Primes {
	public static List<Integer> factorize(int number) {
		if (number < 2) {
			throw new IllegalArgumentException();
		}
		List<Integer> factors = new ArrayList<>();
		int candidate = 2;
		while (number >= candidate) {
			while (number % candidate != 0) {
				if (Math.sqrt(number) < candidate) {
					candidate = number;
				} else {
					candidate++;
				}
			}
			factors.add(candidate);
			number /= candidate;
		}
		return factors;
	}
}
