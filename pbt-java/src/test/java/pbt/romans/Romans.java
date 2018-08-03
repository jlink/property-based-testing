package pbt.romans;

import java.util.*;
import java.util.stream.*;

public class Romans {

	private static final String[] SUBTRACTION_PAIRS = new String[]{"iv", "ix", "xl", "xc", "cd", "cm"};
	private static final Map<Character, Integer> BASE_VALUES = new HashMap<>();

	static {
		BASE_VALUES.put('i', 1);
		BASE_VALUES.put('v', 5);
		BASE_VALUES.put('x', 10);
		BASE_VALUES.put('l', 50);
		BASE_VALUES.put('c', 100);
		BASE_VALUES.put('d', 500);
		BASE_VALUES.put('m', 1000);
	}

	public static int roman2decimal(String roman) {
		int sum = addUpBaseValues(roman);
		return correctForSubtractionPairs(roman, sum);
	}

	private static int correctForSubtractionPairs(String roman, int sum) {
		for (String pair : SUBTRACTION_PAIRS) {
			if (roman.contains(pair)) sum -= 2 * baseValueOf(pair.charAt(0));
		}
		return sum;
	}

	private static int addUpBaseValues(String roman) {
		int sum = 0;
		for (char c : roman.toCharArray()) {
			sum += baseValueOf(c);
		}
		return sum;
	}

	private static int baseValueOf(char letter) {
		Integer value = toDecimalBaseValues().get(letter);
		if (null == value) {
			throw new IllegalArgumentException(String.format("Letter '%s' is not allowed in roman numbers.", letter));
		}
		return value;
	}

	private static Map<Character, Integer> toDecimalBaseValues() {
		return BASE_VALUES;
	}

	public static String decimal2roman(int decimal) {
		Character letter = toRomanBaseValues().get(decimal);
		if (letter != null)
			return String.valueOf(letter);

		String roman = "";
		NavigableSet<Integer> sortedBaseValues = new TreeSet<>(toRomanBaseValues().keySet());
		for (Integer baseValue : sortedBaseValues.descendingSet()) {
			while (decimal >= baseValue) {
				roman += toRomanBaseValues().get(baseValue);
				decimal -= baseValue;
			}
		}
		return roman;
	}

	private static Map<Integer, Character> toRomanBaseValues() {
		return BASE_VALUES.entrySet()
						  .stream()
						  .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
	}
}
