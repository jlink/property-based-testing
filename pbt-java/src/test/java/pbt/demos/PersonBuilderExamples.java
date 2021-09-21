package pbt.demos;

import net.jqwik.api.*;

class PersonBuilderExamples {

	@Property(tries = 50)
	@Report(Reporting.GENERATED)
	boolean anyValidPersonHasAFullName(@ForAll("validPerson") Person aPerson) {
		return aPerson.fullName().length() >= 5;
	}

	@Provide
	Arbitrary<Person> validPerson() {
		Arbitrary<String> firstName = Arbitraries.strings()
												 .withCharRange('a', 'z')
												 .ofMinLength(2).ofMaxLength(10)
												 .map(this::capitalize);
		Arbitrary<String> lastName = Arbitraries.strings()
												.withCharRange('a', 'z')
												.ofMinLength(2).ofMaxLength(20);
		return Builders.withBuilder(Person::new)
				   .use(firstName).inSetter(Person::setFirstName)
				   .use(lastName).inSetter(Person::setLastName)
				   .build();
	}

	private String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	// @Data
	// @NoArgsConstructor
	static class Person {
		private String getFirstName() {
			return firstName;
		}

		private void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		private String getLastName() {
			return lastName;
		}

		private void setLastName(String lastName) {
			this.lastName = lastName;
		}

		private String firstName, lastName;

		public String fullName() {
			return firstName + " " + lastName;
		}

		@Override
		public String toString() {
			return String.format("Person(%s:%s)", firstName, lastName);
		}
	}
}
