package pbt.hwayne;

public class Limit {
	public static Limit of(String category, int amount) {
		return new Limit(category, amount);
	}

	private final String category;
	private final int amount;

	public String category() {
		return category;
	}

	public int amount() {
		return amount;
	}

	private Limit(String category, int amount) {
		this.category = category;
		this.amount = amount;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Limit{");
		sb.append("category='").append(category).append('\'');
		sb.append(", amount=").append(amount);
		sb.append('}');
		return sb.toString();
	}
}
