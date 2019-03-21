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


## Step 6

	@Property
	void center_of_new_board_is_empty(@ForAll @UseType Board board) {
		int center = board.size() / 2 + 1;
		assertThat(board.hole(center, center)).isEqualTo(Hole.EMPTY);
	}

public class Board...
	public Hole hole(int x, int y) {
		if (x == center() && y == center())
			return Hole.EMPTY;
		return Hole.PEG;
	}

	private int center() {
		return size / 2 + 1;
	}

## Step 7

	@Property
	void center_of_new_board_is_empty(@ForAll("newBoards") Board board) {
		int center = board.size() / 2 + 1;
		assertThat(board.hole(center, center)).isEqualTo(Hole.EMPTY);
	}

	@Provide
	Arbitrary<Board> newBoards() {
		return Arbitraries.integers().between(1, 20).filter(i -> i%2 != 0).map(Board::new);
	}


## Step 8

	@Property
	void holes_of_new_board_contain_pegs(@ForAll("newBoards") Board board) {
		Arbitrary<Integer> allX = Arbitraries.integers().between(1, board.size());
		Arbitrary<Integer> allY = Arbitraries.integers().between(1, board.size());

		Arbitrary<Tuple.Tuple2<Integer, Integer>> allXandY = Combinators.combine(allX, allY).as(Tuple::of);

		allXandY.allValues().ifPresent(
				stream -> stream.forEach(xAndY -> {
					int x = xAndY.get1();
					int y = xAndY.get2();
					assertThat(board.hole(x, y)).isEqualTo(Hole.PEG);
				})
		);

		//Assume.that(x != 3 || y != 3);
	}

    => Fails due to center
    
    
## Step 9

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

    ==> Delete @Group OfSize5: No longer needed
    
## Step 10

	@Example
	void peg_can_be_removed() {
		Board board = new Board(3);
		board.removePeg(1, 1);
		assertThat(board.hole(1, 1)).isEqualTo(Hole.EMPTY);
	}


public class Board {
	private boolean removed = false;

	public Hole hole(int x, int y) {
		if (x == center() && y == center())
			return Hole.EMPTY;
		return removed ? Hole.EMPTY : Hole.PEG;
	}

	public void removePeg(int x, int y) {
		removed = true;
	}
}


## Step 11: Translate example into property

	@Property
	void peg_can_be_removed(@ForAll("boardsWithPosition") Tuple3<Board, Integer, Integer> boardWithPosition) {
		Board board = boardWithPosition.get1();
		Integer x = boardWithPosition.get2();
		Integer y = boardWithPosition.get3();

		Assume.that(x != center(board) || y != center(board));

		board.removePeg(x, y);
		assertThat(board.hole(x, y)).isEqualTo(Hole.EMPTY);
	}

	@Provide
	Arbitrary<Tuple3<Board, Integer, Integer>> boardsWithPosition() {
		Arbitrary<Board> boards = newBoards();
		return boards.flatMap(board -> {
			Arbitrary<Integer> xs = Arbitraries.integers().between(1, board.size());
			Arbitrary<Integer> ys = Arbitraries.integers().between(1, board.size());
			return Combinators.combine(boards, xs, ys).as(Tuple::of);
		});
	}


## Step 12: Let property check more

### Refactoring

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

### Enhance checking

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

==> Property fails because implementation is not adequate

## Step 13: Introduce real storage of holes

public class Board implements Serializable {
	private final int size;
	private List<Hole> holes = new ArrayList<>();

	public Board(int size) {
		if (size < 1 || size % 2 == 0)
			throw new IllegalArgumentException("Only boards of odd size >= 1 allowed");
		this.size = size;
		initHoles(size);
	}

	private void initHoles(int size) {
		for (int i = 0; i < size * size; i++) {
			holes.add(Hole.PEG);
		}
		removePeg(center(), center());
	}

	public Hole hole(int x, int y) {
		return holes.get(calculateIndex(x, y));
	}

	public void removePeg(int x, int y) {
		int index = calculateIndex(x, y);
		holes.set(index, Hole.EMPTY);
	}

	private int calculateIndex(int x, int y) {
		return (x-1) * size + (y -1);
	}
}
