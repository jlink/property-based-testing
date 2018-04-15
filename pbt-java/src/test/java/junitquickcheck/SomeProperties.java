package junitquickcheck;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.InRange;
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
	public void shouldShrinkTo11ButDoesNot(
			@When(seed = -4386629332000517955L) //
			@InRange(minInt = 1, maxInt = 1000) int i
	) {
		Assume.assumeTrue(i > 10);
		Assert.assertTrue(i % 2 == 0);
	}
}