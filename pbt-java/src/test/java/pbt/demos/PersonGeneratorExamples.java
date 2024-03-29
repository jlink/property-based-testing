package pbt.demos;

import net.jqwik.api.*;

class PersonGeneratorExamples {

	@Property(tries = 50)
	@Report(Reporting.GENERATED)
	boolean anyValidPersonHasAFullName(@ForAll("validPeople") Person aPerson) {
		return aPerson.fullName().length() >= 5;
	}

	@Provide
	Arbitrary<Person> validPeople() {
		Arbitrary<String> firstName = firstNames();
		Arbitrary<String> lastName = lastNames();
		return Combinators.combine(firstName, lastName).as(Person::new);
	}

	private Arbitrary<String> lastNames() {
		return Arbitraries.strings()
						  .withCharRange('a', 'z')
						  .ofMinLength(2).ofMaxLength(20);
	}

	private Arbitrary<String> firstNames() {
		return Arbitraries.strings()
					  .withCharRange('a', 'z')
					  .ofMinLength(2).ofMaxLength(10)
					  .map(this::capitalize);
	}

	private String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	static class Person {
		private final String firstName, lastName;

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
