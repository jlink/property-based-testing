package pbt.patterns;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import org.assertj.core.api.*;

import java.util.*;
import java.util.stream.*;

class Commutativity {

	@Property @Report(Reporting.GENERATED)
	void sortingAndFilteringAreCommutative(@ForAll List<@AlphaChars String> listOfNames) {
		List<String> filteredThenSorted = listOfNames.stream()
				.filter(name -> !name.toLowerCase().contains("a"))
				.sorted()
				.collect(Collectors.toList());

		List<String> sortedThenFiltered = listOfNames.stream()
				.sorted()
				.filter(name -> !name.toLowerCase().contains("a"))
				.collect(Collectors.toList());

		Assertions.assertThat(filteredThenSorted).isEqualTo(sortedThenFiltered);
	}

}
