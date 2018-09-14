package pbt.web;

import java.lang.annotation.*;
import java.util.*;
import java.util.stream.*;

import com.fasterxml.jackson.core.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuples.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;

import static net.jqwik.api.Arbitraries.*;

public class JsonArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		if (!targetType.isOfType(String.class))
			return false;
		if (isAnnotated(targetType, JsonArray.class)) {
			return true;
		}
		if (isAnnotated(targetType, JsonObject.class)) {
			return true;
		}
		return false;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Set<Arbitrary<?>> arbitraries = new HashSet<>();
		if (isAnnotated(targetType, JsonArray.class)) {
			arbitraries.add(jsonArray());
		}
		if (isAnnotated(targetType, JsonObject.class)) {
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
		return lazy(() -> {
			IntegerArbitrary numberOfProperties = integers().between(1, 5);
			return numberOfProperties
						   .flatMap(props -> {
										List<Arbitrary<Tuple2<String, String>>> entries =
												IntStream.range(0, props)
														 .mapToObj(i -> Combinators.combine(jsonKey(), jsonValue()).as(Tuples::tuple))
														 .collect(Collectors.toList());
										return Combinators.combine(entries).as(keysAndValues -> {
											String propString = keysAndValues
																		.stream()
																		.map(entry -> String.format("\"%s\": %s", entry.get1(), entry.get2()))
																		.collect(Collectors.joining(","));
											return "{ " + propString + " }";
										});
									}

						   );
		});

//		return lazy(() -> {
//			Arbitrary<String> key = jsonKey();
//			Arbitrary<String> value = oneOf(jsonArray(), jsonNumber(), jsonString(), jsonObject());
//			return flatCombine(key, value, (k, v) -> constant(String.format("{ \"%s\": %s}", k, v)));
//		});
	}

	private Arbitrary<String> jsonValue() {
		return oneOf(jsonArray(), jsonNumber(), jsonNumber(), jsonString(), jsonString(), jsonBoolean(), jsonNull(), jsonObject());
	}

	private Arbitrary<String> jsonBoolean() {
		return of("true", "false");
	}

	private Arbitrary<String> jsonNull() {
		return constant("null");
	}

	private Arbitrary<String> jsonKey() {
		return strings().ofMinLength(1).ofMaxLength(20).alpha().numeric().withChars('_', '.', '-');
	}

	//TODO: Replace with Combinators.flatCombine as soon as available
	private Arbitrary<String> flatCombine(Arbitrary<String> key, Arbitrary<String> value, Combinators.F2<String, String, Arbitrary<String>> combinator) {
		return key.flatMap(k -> value.flatMap( v -> combinator.apply(k, v)));
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

	// TODO: Replace with targetType.isAnnotated() as soon as available in jqwik
	private boolean isAnnotated(TypeUsage targetType, Class<? extends Annotation> annotationType) {
		for (Annotation annotation : targetType.getAnnotations()) {
			if (annotation.annotationType().equals(annotationType))
				return true;
		}
		return false;
	}

	@Override
	public int priority() {
		return 1;
	}
}
