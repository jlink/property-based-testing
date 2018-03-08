package pbt.demos;

import java.util.ArrayList;
import java.util.*;

import net.jqwik.api.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Label("Collections.reverse")
class ListReverseTests {

	@Example
	@Label("3 elements")
	void reverseList() {
		List<Integer> aList = asList(1, 2, 3);
		Collections.reverse(aList);
		assertThat(aList).containsExactly(3, 2, 1);
	}

	@Group
	@Label("reverse twice is original")
	class ReverseTwice {

		@Example
		void emptyList() {
			List<Integer> aList = Collections.emptyList();
			assertThat(reverse(reverse(aList))).isEqualTo(aList);
		}

		@Example
		void oneElement() {
			List<Integer> aList = Collections.singletonList(1);
			assertThat(reverse(reverse(aList))).isEqualTo(aList);
		}

		@Example
		void manyElements() {
			List<Integer> aList = asList(1, 2, 3, 4, 5, 6, 7);
			assertThat(reverse(reverse(aList))).isEqualTo(aList);
		}

		private <T> List<T> reverse(List<T> original) {
			List<T> clone = new ArrayList<>(original);
			Collections.reverse(clone);
			return clone;
		}
	}


}
