package pbt.patterns;

import net.jqwik.api.*;

class BusinessRuleAsProperty {

	@Property
	@Label("High volume customer discount is 5% for total above 500 EUR")
	boolean highVolumeCustomerDiscount(@ForAll Euro lastYearVolume, @ForAll Euro invoiceTotal) {
		Assume.that(lastYearVolume.compareTo(Euro.amount(5000)) > 0);
		Assume.that(invoiceTotal.compareTo(Euro.amount(500)) >= 0);
		return new DiscountCalculator().discountInPercent(lastYearVolume, invoiceTotal).equals(new Percent(5));
	}

	@Property
	@Label("No discount for low volume customer or total below 500 EUR")
	boolean noDiscount(@ForAll Euro lastYearVolume, @ForAll Euro invoiceTotal) {
		Assume.that(
				lastYearVolume.compareTo(Euro.amount(5000)) <= 0 ||
						invoiceTotal.compareTo(Euro.amount(500)) < 0);
		return new DiscountCalculator().discountInPercent(lastYearVolume, invoiceTotal).equals(new Percent(0));
	}
}

class Euro implements Comparable<Euro> {
	public static Euro amount(int amount) {
		return new Euro();
	}

	@Override
	public int compareTo(Euro o) {
		return 0;
	}
}

class DiscountCalculator {
	public Percent discountInPercent(Euro lastYearVolume, Euro invoiceTotal) {
		return new Percent(5);
	}
}

class Percent {
	public Percent(int percent) {
	}
}