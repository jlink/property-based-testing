package pbt.demos;

import net.jqwik.api.*;

class PersonGeneratorExamples {

	@Property
	boolean anyValidPersonHasAFullName(@ForAll Person aPerson) {
		return aPerson.fullName().length() > 0;
	}

	@Provide
	Arbitrary<Person> validPerson() {
		Arbitrary<String> firstName = Arbitraries.string('a', 'z', 10);
		Arbitrary<String> lastName = Arbitraries.string('a', 'z', 20);
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
	}
}
