package pbt.solitaire;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
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
		forAllPositions(board, xAndY -> {
			int x = xAndY.get1();
			int y = xAndY.get2();
			if (x == center(board) && y == center(board)) {
				return;
			}
			assertThat(board.hole(x, y)).isEqualTo(Hole.PEG);
		});
	}

	private void forAllPositions(@ForAll("newBoards") Board board, Consumer<Tuple2<Integer, Integer>> asserter) {
		Arbitrary<Integer> allX = Arbitraries.integers().between(1, board.size());
		Arbitrary<Integer> allY = Arbitraries.integers().between(1, board.size());

		Arbitrary<Tuple2<Integer, Integer>> allXandY = Combinators.combine(allX, allY).as(Tuple::of);

		allXandY.allValues().ifPresent(
				stream -> {
					stream.forEach(asserter);
				}
		);
	}

	@Property
	void peg_can_be_removed(@ForAll("boardsWithPosition") Tuple3<Board, Integer, Integer> boardWithPosition) {
		Board board = boardWithPosition.get1();
		Integer x = boardWithPosition.get2();
		Integer y = boardWithPosition.get3();

		Assume.that(x != center(board) || y != center(board));

		board.removePeg(x, y);
		assertThat(board.hole(x, y)).isEqualTo(Hole.EMPTY);

		forAllPositions(board, xAndY -> {
			if (!xAndY.equals(Tuple.of(x, y)) && !xAndY.equals(Tuple.of(center(board), center(board)))) {
				assertThat(board.hole(xAndY.get1(), xAndY.get2())).isEqualTo(Hole.PEG);
			}
		});
	}

	@Provide
	Arbitrary<Tuple3<Board, Integer, Integer>> boardsWithPosition() {
		Arbitrary<Board> boards = newBoards();
		return boards.flatMap(board -> {
			Arbitrary<Integer> xs = Arbitraries.integers().between(1, board.size());
			Arbitrary<Integer> ys = Arbitraries.integers().between(1, board.size());
			return Combinators.combine(xs, ys).as((x, y) -> Tuple.of(board, x, y));
		});
	}

	private int center(@ForAll("newBoards") Board board) {
		return board.size() / 2 + 1;
	}

	@Provide
	Arbitrary<Board> newBoards() {
		return Arbitraries.integers().between(5, 20).filter(i -> i % 2 != 0).map(Board::new);
	}

}

