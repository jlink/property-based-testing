package pbt.romans;

public class Romans {
	public static int roman2decimal(String roman) {
		if (roman.length() == 2) {
			return baseValueOf(roman.charAt(0)) + baseValueOf(roman.charAt(1));
		}
		char first = roman.charAt(0);
		return baseValueOf(first);
	}

	private static int baseValueOf(char letter) {
		switch (letter) {
			case 'i': return 1;
			case 'v': return 5;
			case 'x': return 10;
		}
		throw new IllegalArgumentException(String.format("Letter '%s' is not allowed in roman numbers.", letter));
	}
}
