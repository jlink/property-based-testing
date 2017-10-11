package pbt.romans;

public class Romans {
	public static int roman2decimal(String roman) {
		int sum = 0;
		for (char c : roman.toCharArray()) {
			sum += baseValueOf(c);
		}
		return sum;
	}

	private static int baseValueOf(char letter) {
		switch (letter) {
			case 'i':
				return 1;
			case 'v':
				return 5;
			case 'x':
				return 10;
		}
		throw new IllegalArgumentException(String.format("Letter '%s' is not allowed in roman numbers.", letter));
	}
}
