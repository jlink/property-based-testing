package pbt.recursive;

public class Leaf implements Tree {

	private final String label;

	public Leaf(String label) {
		this.label = label;
	}

	@Override
	public int countLeaves() {
		return 1;
	}

	@Override
	public String toString() {
		return label;
	}
}
