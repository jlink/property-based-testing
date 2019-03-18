package pbt.solitaire;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Group
@Label("Boards")
class BoardProperties {

	@Property
	void can_create_board_with_any_odd_size(@ForAll @IntRange(min = -10, max = 10) int size) {
		if (size > 0 && size % 2 != 0) {
			assertThat(new Board(size).size()).isEqualTo(size);
		} else {
			assertThatThrownBy(() -> new Board(size)).isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Property
	void center_of_new_board_is_empty(@ForAll("newBoards") Board board) {
		int center = center(board);
		assertThat(board.hole(center, center)).isEqualTo(Hole.EMPTY);
	}

	@Property
	void holes_of_new_board_contain_pegs(@ForAll("newBoards") Board board) {
		Arbitrary<Integer> allX = Arbitraries.integers().between(1, board.size());
		Arbitrary<Integer> allY = Arbitraries.integers().between(1, board.size());

		Arbitrary<Tuple.Tuple2<Integer, Integer>> allXandY = Combinators.combine(allX, allY).as(Tuple::of);

		allXandY.allValues().ifPresent(
				stream -> stream.forEach(xAndY -> {
					int x = xAndY.get1();
					int y = xAndY.get2();
					if (x == center(board) && y == center(board)) {
						return;
					}
					assertThat(board.hole(x, y)).isEqualTo(Hole.PEG);
				})
		);
	}

	private int center(@ForAll("newBoards") Board board) {
		return board.size() / 2 + 1;
	}

	@Provide
	Arbitrary<Board> newBoards() {
		return Arbitraries.integers().between(1, 20).filter(i -> i % 2 != 0).map(Board::new);
	}

}

