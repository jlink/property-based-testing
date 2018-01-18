package pbt.jmarticle;

import java.util.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CollectionsTests {

	@Test
	void reverseList() {
		List<Integer> aList = Arrays.asList(1, 2, 3);
		Collections.reverse(aList);
		Assertions.assertThat(aList).containsExactly(3, 2, 1);
	}

}
