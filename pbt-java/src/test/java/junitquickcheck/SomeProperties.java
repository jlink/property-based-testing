package junitquickcheck;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.generator.java.lang.IntegerGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class SomeProperties {
	@Property
	public void concatenationLength(String s1, String s2) {
		assertEquals(s1.length() + s2.length(), (s1 + s2).length());
	}

	@Property()
	public void shouldShrinkTo101ButDoesNot(
			@When(seed = -4386629332000517955L) //
			@InRange(minInt = 1, maxInt = 1000) int i
	) {
		Assume.assumeTrue(i > 100);
		Assert.assertTrue(i % 2 == 0);
	}


	@Property()
	public void shouldShrinkTo101String(@From(NumericString.class) String iString) {
		int i = Integer.parseInt(iString);
		Assert.assertTrue(i % 2 == 0);
	}

	public static class NumericString extends Generator<String> {

		public NumericString() {
			super(String.class);
		}

		@Override
		public String generate(SourceOfRandomness random, GenerationStatus status) {
			return String.valueOf(random.nextInt(100, 1000));
		}
	}
}