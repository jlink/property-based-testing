package pbt.patterns;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.stream.*;

class Commutativity {

	@Property @Report(Reporting.GENERATED)
	void sortingAndFilteringAreCommutative(@ForAll("names") List<String> listOfNames) {
		List<String> filteredThenSorted = listOfNames.stream() //
				.filter(name -> !name.contains("a")) //
				.sorted() //
				.collect(Collectors.toList());

		List<String> sortedThenFiltered = listOfNames.stream() //
				.sorted() //
				.filter(name -> !name.contains("a")) //
				.collect(Collectors.toList());

		Assertions.assertThat(filteredThenSorted).isEqualTo(sortedThenFiltered);
	}

	@Provide
	Arbitrary<List<String>> names() {
		Arbitrary<String> name = Arbitraries.strings() //
				.withCharRange('a', 'z') //
				.ofMinLength(3).ofMaxLength(40);
		return name.list().ofMinSize(0).ofMaxSize(30);
	}

}
