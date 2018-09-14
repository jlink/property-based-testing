package pbt.web;

import java.lang.annotation.*;
import java.util.*;

import com.fasterxml.jackson.core.util.*;

import net.jqwik.api.*;
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
		return false;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Set<Arbitrary<?>> arbitraries = new HashSet<>();
		if (isAnnotated(targetType, JsonArray.class)) {
			arbitraries.add(arrays());
		}
		return arbitraries;
	}

	private Arbitrary<String> arrays() {
		return oneOf(
				jsonArray(jsonNumber()),
				jsonArray(jsonString())
		);
	}

	private Arbitrary<String> jsonArray(Arbitrary<String> json) {
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
		return Arbitraries.doubles().map(d -> Double.toString(d));
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
