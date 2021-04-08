package pbt.hwayne;

public class Bill {
	public static Bill of(Item... items) {
		return new Bill(items);
	}

	public Bill(Item[] items) {

	}
}
