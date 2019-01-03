package junitquickcheck;

import org.junit.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

public class JupiterAndVintageTests {


	@Before
	@BeforeEach
	public void commonSetup() {
		System.out.println("before");
	}

	@Test
	void aJupiterTest() {
		System.out.println("jupiter test");
	}

	@org.junit.Test
	public void aVintageTest() {
		System.out.println("vintage test");
	}
}
