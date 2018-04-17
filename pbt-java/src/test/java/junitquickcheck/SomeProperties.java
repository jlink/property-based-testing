package junitquickcheck;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
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
	public void shouldShrinkTo105(
			@From(DivisibleBy5.class) //
			@When(seed = -4386629332000517955L) //
					int i //
	) {
		Assert.assertTrue(i % 2 == 0);
	}

	public static class DivisibleBy5 extends Generator<Integer> {

		public DivisibleBy5() {
			super(Integer.class);
		}

		@Override
		public Integer generate(SourceOfRandomness random, GenerationStatus status) {
			return gen().type(int.class) //
					.filter(i -> i > 100) //
					.filter(i -> i < 1000) //
					.map(i -> Math.abs(i * 5)) //
					.generate(random, status);
		}
	}
}