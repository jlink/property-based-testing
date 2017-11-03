package pbt.demos;

import net.jqwik.api.*;

class PersonGeneratorExamples {

	@Property(tries = 50, reporting = ReportingMode.GENERATED)
	boolean anyValidPersonHasAFullName(@ForAll Person aPerson) {
		return aPerson.fullName().length() > 0;
		// return aPerson.fullName().trim().length() > 0;
	}

	@Provide
	Arbitrary<Person> validPerson() {
		Arbitrary<String> firstName = Arbitraries.string('a', 'z', 1, 10);
		Arbitrary<String> lastName = Arbitraries.string('a', 'z', 1, 20);
		return Combinators.combine(firstName, lastName).as((first, last) -> new Person(first, last));
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