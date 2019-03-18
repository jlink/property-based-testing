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

	@Group
	class OfSize5 {

		private Board board = new Board(5);

		@Property
		void all_holes_of_new_board_contain_pegs_except_center(
				@ForAll @IntRange(min = 1, max = 5) int x,
				@ForAll @IntRange(min = 1, max = 5) int y
		) {
			Assume.that(x != 3 || y != 3);
			assertThat(board.hole(x, y)).isEqualTo(Hole.PEG);
		}

		@Example
		void center_is_empty() {
			assertThat(board.hole(3, 3)).isEqualTo(Hole.EMPTY);
		}

	}
}

