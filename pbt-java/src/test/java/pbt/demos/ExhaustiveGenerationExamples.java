package pbt.demos;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class ExhaustiveGenerationExamples {

	@Property
	void allChessSquares(
			@ForAll @CharRange(from = 'a', to = 'h') char column,
			@ForAll @CharRange(from = '1', to = '8')char row
	) {
		String square = column + "" + row;
		System.out.println(square);
	}

	// Will generate 532441 different matrices
	@Property(generation = GenerationMode.EXHAUSTIVE)
	void matrix3times3(@ForAll @Size(3) List< @Size(3) List<@IntRange(min = 1, max = 9) Integer>> matrix) {
		System.out.println(format(matrix));
	}

	private String format(List<List<Integer>> matrix) {
		return String.format("%s%n%s%n%s%n", matrix.get(0), matrix.get(1), matrix.get(2));
	}
}
