package pbt.demos;

import net.jqwik.api.*;

class PersonGeneratorExamples {

	@Property(tries = 50, reporting = ReportingMode.GENERATED)
	boolean anyValidPersonHasAFullName(@ForAll Person aPerson) {
		return aPerson.fullName().length() >= 5;
	}

	@Provide
	Arbitrary<Person> validPerson() {
		Arbitrary<Character> initialChar = Arbitraries.chars('A', 'Z');
		Arbitrary<String> firstName = //
				Arbitraries.strings('a', 'z', 2, 10);
		Arbitrary<String> lastName = //
				Arbitraries.strings('a', 'z', 2, 20);
		return Combinators.combine(initialChar, firstName, lastName) //
				.as((initial, first, last) -> new Person(initial + first, last));
	}

	static class Person {
		private final String firstName;
		private final String lastName;

		Person(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String fullName() {
			return firstName + " " + lastName;
		}

		@Override
		public String toString() {
			return String.format("Person(%s:%s)", firstName, lastName);
		}
	}
}
