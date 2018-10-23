package pbt.demos;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class ExhaustiveGenerationExamples {

	@Property(generation = GenerationMode.EXHAUSTIVE)
	void allChessSquares(
			@ForAll @CharRange(from = 'a', to = 'h') char column,
			@ForAll @CharRange(from = '1', to = '8')char row
	) {
		String square = column + "" + row;
		System.out.println(square);
	}

}
