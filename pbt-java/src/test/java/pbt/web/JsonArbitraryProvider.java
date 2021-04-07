package pbt.web;

import java.util.*;
import java.util.stream.*;

import com.fasterxml.jackson.core.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;

import static net.jqwik.api.Arbitraries.of;
import static net.jqwik.api.Arbitraries.*;

public class JsonArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		if (!targetType.isOfType(String.class))
			return false;
		if (targetType.isAnnotated(JsonArray.class)) {
			return true;
		}
		if (targetType.isAnnotated(JsonObject.class)) {
			return true;
		}
		return false;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Set<Arbitrary<?>> arbitraries = new HashSet<>();
		if (targetType.isAnnotated(JsonArray.class)) {
			arbitraries.add(jsonArray());
		}
		if (targetType.isAnnotated(JsonObject.class)) {
			arbitraries.add(jsonObject());
		}
		return arbitraries;
	}

	private Arbitrary<String> jsonArray() {
		return oneOf(
			array(jsonNumber()),
			array(jsonNumber()),
			array(jsonString()),
			array(jsonString()),
			array(jsonBoolean()),
			array(jsonObject())
		);
	}

	private Arbitrary<String> jsonObject() {
		// Must be lazy due to recursive creation of json object arbitraries
		return lazy(() -> {
			IntegerArbitrary numberOfProperties = integers().between(0, 5);
			return numberOfProperties
					   .flatMap(props -> {
						   Arbitrary<List<String>> propNames = jsonKey().list().ofSize(props).uniqueElements();
						   Arbitrary<List<String>> propValues = jsonValue().list().ofSize(props);
						   return Combinators.combine(propNames, propValues)
											 .as((keys, values) -> {
												 List<Tuple2<String, String>> keysAndValues = new ArrayList<>();
												 for (int i = 0; i < keys.size(); i++) {
													 String key = keys.get(i);
													 String value = values.get(i);
													 keysAndValues.add(Tuple.of(key, value));
												 }
												 return "{ " + objectBody(keysAndValues) + " }";
											 });
					   });
		});
	}

	private String objectBody(List<Tuple2<String, String>> keysAndValues) {
		return keysAndValues.stream()
							.map(entry -> String.format("\"%s\":%s", entry.get1(), entry.get2()))
							.collect(Collectors.joining(", "));
	}

	private Arbitrary<String> jsonValue() {
		return oneOf(jsonArray(), jsonNumber(), jsonNumber(), jsonString(), jsonString(), jsonBoolean(), jsonNull(), jsonObject());
	}

	private Arbitrary<String> jsonBoolean() {
		return of("true", "false");
	}

	private Arbitrary<String> jsonNull() {
		return just("null");
	}

	private Arbitrary<String> jsonKey() {
		return strings().ofMinLength(1).ofMaxLength(20).alpha().numeric().withChars('_', '.', '-');
	}

	private Arbitrary<String> array(Arbitrary<String> json) {
		return json.list().ofMaxSize(100).map(list -> "[" + String.join(", ", list) + "]");
	}

	private Arbitrary<String> jsonString() {
		return Arbitraries.strings()
						  .alpha()
						  .numeric()
						  .withChars('.', ' ', ':', '"')
						  .map(s -> new String(BufferRecyclers.quoteAsJsonText(s)))
						  .map(s -> String.format("\"%s\"", s));
	}

	private Arbitrary<String> jsonNumber() {
		return Arbitraries.doubles().ofScale(4).map(d -> Double.toString(d));
	}

	@Override
	public int priority() {
		// Must be larger than 0 to replace default String arbitrary
		return 1;
	}
}
