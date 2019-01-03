package junitquickcheck;

import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import com.pholser.junit.quickcheck.runner.*;
import org.junit.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.*;

@RunWith(JUnitQuickcheck.class)
public class JupiterAndQuickcheckTests {


	@Before
	@BeforeEach
	public void commonSetup() {
		System.out.println("before");
	}

	@Test
	void aJupiterTest() {
		System.out.println("jupiter test");
	}

	@Property
	public void aJunitQuickcheckProperty(@InRange(minInt = 1, maxInt = 1000) int i) {
		System.out.println("junit-quickcheck property");
	}
}
