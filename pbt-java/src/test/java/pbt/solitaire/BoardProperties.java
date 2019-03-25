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
		Position center = board.center();
		assertThat(board.hole(center)).isEqualTo(Hole.EMPTY);
	}

	@Property
	void holes_of_new_board_contain_pegs(@ForAll("newBoards") Board board) {
		forAllPositions(board, position -> {
			if (board.isCenter(position)) {
				return;
			}
			assertThat(board.hole(position)).isEqualTo(Hole.PEG);
		});
	}

	private void forAllPositions(Board board, Consumer<Position> asserter) {
		Arbitrary<Integer> allX = Arbitraries.integers().between(1, board.size());
		Arbitrary<Integer> allY = Arbitraries.integers().between(1, board.size());
		Arbitrary<Position> allPositions = Combinators.combine(allX, allY).as(Position::xy);

		allPositions.allValues().ifPresent(
				stream -> {
					stream.forEach(asserter);
				}
		);
	}

	@Property
	void peg_can_be_removed(@ForAll("boardsWithPosition") Tuple2<Board, Position> boardWithPosition) {
		Board board = boardWithPosition.get1();
		Position position = boardWithPosition.get2();

		Assume.that(!board.isCenter(position));

		board.removePeg(position);
		assertThat(board.hole(position)).isEqualTo(Hole.EMPTY);

		forAllPositions(board, each -> {
			if (!each.equals(position) && !board.isCenter(each)) {
				assertThat(board.hole(each)).isEqualTo(Hole.PEG);
			}
		});
	}

	@Provide
	Arbitrary<Tuple2<Board, Position>> boardsWithPosition() {
		Arbitrary<Board> boards = newBoards();
		return boards.flatMap(board -> {
			Arbitrary<Integer> xs = Arbitraries.integers().between(1, board.size());
			Arbitrary<Integer> ys = Arbitraries.integers().between(1, board.size());
			return Combinators.combine(xs, ys).as((x, y) -> Tuple.of(board, Position.xy(x, y)));
		});
	}

	@Provide
	Arbitrary<Board> newBoards() {
		return Arbitraries.integers().between(1, 20).filter(i -> i % 2 != 0).map(Board::new);
	}

}

