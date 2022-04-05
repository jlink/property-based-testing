package pbt.demos;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class ExhaustiveGenerationExamples {

	@Property
	void allChessSquares(
			@ForAll @CharRange(from = 'a', to = 'h') char column,
			@ForAll @CharRange(from = '1', to = '8') char row
	) {
		String square = column + "" + row;
		System.out.println(square);
	}

	// Generates all 362880 mini sudokus of size 3x3
	@Property(generation = GenerationMode.EXHAUSTIVE)
	void miniSudokus(@ForAll("sudokus") List<List<Integer>> sudoku) {
		System.out.println(format(sudoku));
	}

	@Provide
	Arbitrary<List<List<Integer>>> sudokus() {
		return Arbitraries.shuffle(1, 2, 3, 4, 5, 6, 7, 8, 9)
						  .map(list -> Arrays.asList(list.subList(0, 3), list.subList(3, 6), list.subList(6, 9)));
	}

	private String format(List<List<Integer>> matrix) {
		return String.format("%s%n%s%n%s%n", matrix.get(0), matrix.get(1), matrix.get(2));
	}
}
