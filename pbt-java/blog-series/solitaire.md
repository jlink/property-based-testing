## Step 1

class BoardProperties {

	@Example
	void all_holes_of_new_board_contain_pegs() {
		Board board = new Board(5, 5);

		Assertions.assertThat(board.hole(1, 1)).isEqualTo(Hole.PEG);
		Assertions.assertThat(board.hole(5, 5)).isEqualTo(Hole.PEG);
	}
}

public class Board {
	public Board(int width, int height) {}

	public Hole hole(int x, int y) {
		return Hole.PEG;
	}
}

public enum Hole {
	PEG
}


## Step 2

class BoardProperties {

	@Example
	void all_holes_of_new_board_contain_pegs_except_center() {
		Board board = new Board(5, 5);

		assertThat(board.hole(1, 1)).isEqualTo(Hole.PEG);
		assertThat(board.hole(5, 5)).isEqualTo(Hole.PEG);

		assertThat(board.hole(3, 3)).isEqualTo(Hole.EMPTY);
	}
}

public class Board {
	public Board(int width, int height) {}

	public Hole hole(int x, int y) {
		if (x == 3 && y == 3)
			return Hole.EMPTY;
		return Hole.PEG;
	}
}

public enum Hole {
	EMPTY, PEG
}


## Step 3

@Group
@Label("Boards")
class BoardProperties {

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


## Step 4

class BoardProperties...

	@Example
	void can_create_board_with_any_odd_size() {
		assertThat(new Board(1).size()).isEqualTo(1);
		assertThat(new Board(3).size()).isEqualTo(3);
		assertThat(new Board(11).size()).isEqualTo(11);
	}

	@Example
	void cannot_create_boards_with_even_size() {
		assertThatThrownBy(() -> new Board(-3)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new Board(2)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new Board(10)).isInstanceOf(IllegalArgumentException.class);
	}


public class Board {
	private final int size;

	public Board(int size) {
		if (size < 1 || size % 2 == 0)
			throw new IllegalArgumentException("Only boards of odd size >= 1 allowed");
		this.size = size;
	}

	public Hole hole(int x, int y) {
		if (x == 3 && y == 3)
			return Hole.EMPTY;
		return Hole.PEG;
	}

	public int size() {
		return size;
	}
}


## Step 5

	@Property
	void can_create_board_with_any_odd_size(@ForAll @IntRange(min = -10, max = 10) int size) {
		if (size > 0 && size % 2 != 0) {
			assertThat(new Board(size).size()).isEqualTo(size);
		} else {
			assertThatThrownBy(() -> new Board(size)).isInstanceOf(IllegalArgumentException.class);
		}
	}

