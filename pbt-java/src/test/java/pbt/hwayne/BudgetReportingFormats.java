package pbt.hwayne;

import java.util.*;

import net.jqwik.api.*;

public interface BudgetReportingFormats {

	class Bills implements SampleReportingFormat {

		@Override
		public boolean appliesTo(Object value) {
			return value instanceof Bill;
		}

		@Override
		public Object report(Object value) {
			Bill bill = (Bill) value;
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("items", bill.items());
			return attributes;
		}
	}

	class Items implements SampleReportingFormat {

		@Override
		public boolean appliesTo(Object value) {
			return value instanceof Item;
		}

		@Override
		public Object report(Object value) {
			Item item = (Item) value;
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("singleCost", item.singleCost());
			attributes.put("count", item.count());
			item.category().ifPresent(category -> attributes.put("category", category));
			return attributes;
		}
	}

	class Limits implements SampleReportingFormat {

		@Override
		public boolean appliesTo(Object value) {
			return value instanceof Limit;
		}

		@Override
		public Object report(Object value) {
			Limit limit = (Limit) value;
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("category", limit.category());
			attributes.put("amount", limit.amount());
			return attributes;
		}
	}
}
