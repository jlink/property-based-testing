package pbt.patterns;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.stream.*;

class Commutativity {

	@Property(reporting = Reporting.GENERATED)
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
		Arbitrary<String> name = Arbitraries.strings('a', 'z', 3, 40);
		return Arbitraries.listOf(name, 0, 30);
	}

}
