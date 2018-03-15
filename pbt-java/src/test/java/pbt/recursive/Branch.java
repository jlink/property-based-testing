package pbt.recursive;

public class Branch implements Tree {

	private final Tree left;
	private final Tree right;

	public Branch(Tree left, Tree right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int countLeaves() {
		return left.countLeaves() + right.countLeaves();
	}

	@Override
	public String toString() {
		return String.format("[%s|%s]", left, right);
	}

	public Tree left() {
		return left;
	}

	public Tree right() {
		return right;
	}
}
