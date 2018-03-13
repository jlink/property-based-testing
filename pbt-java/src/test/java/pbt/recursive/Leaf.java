package pbt.recursive;

public class Leaf implements Tree {
	@Override
	public int countLeaves() {
		return 1;
	}

	@Override
	public String toString() {
		return "+";
	}
}
