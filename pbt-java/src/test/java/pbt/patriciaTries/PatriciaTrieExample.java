/*
 * Copyright (c) 2019, The Regents of the University of California
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pbt.patriciaTries;

import java.util.*;

import org.apache.commons.collections4.*;
import org.apache.commons.collections4.trie.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.constraints.*;

import static org.junit.Assert.*;

/**
 * @author Rohan Padhye
 * <p>
 * Shamelessly stolen from https://github.com/rohanpadhye/jqf/blob/master/README.md#example
 *
 * This should discover a bug (apache commons-collections 4.4.3)
 * https://issues.apache.org/jira/browse/COLLECTIONS-714
 */
public class PatriciaTrieExample {

	@Example
	void failingExample() {

		Map<@StringLength(max = 10) String, Integer> map = new HashMap<>();
		map.put("x", 1);
		map.put("x\0", 2);

		String key = "x";

		Trie trie = new PatriciaTrie(map);

		assertTrue(trie.containsKey(key));
	}

	@Property(tries = 10000, maxDiscardRatio = 100, afterFailure = AfterFailureMode.RANDOM_SEED)
	void testMap2Trie(
			@ForAll Map<@StringLength(min = 1, max = 10) String, Integer> map,
			@ForAll @StringLength(min = 1, max = 10) String key
	) {

		// Key should exist in map
		Assume.that(map.containsKey(key));   // the test is invalid if this predicate is not true

		// Create new trie with input `map`
		Trie trie = new PatriciaTrie(map);

		// The key should exist in the trie as well
		assertTrue(trie.containsKey(key));  // fails when map = {"x": 1, "x\0": 2} and key = "x"
	}

	@Property
	void improvedMap2Trie(@ForAll("mapsWithKeyAlreadyIn") Tuple2<Map<String, Integer>, String> mapAndKey) {
		Map<String, Integer> map = mapAndKey.get1();
		String key = mapAndKey.get2();

		// Key should exist in map
		// generator makes sure that's always true
		Assume.that(map.containsKey(key));

		// Create new trie with input `map`
		Trie trie = new PatriciaTrie(map);

		// The key should exist in the trie as well
		assertTrue(trie.containsKey(key));  // fails when map = {"x": 1, "x\0": 2} and key = "x"
	}

	@Provide
	Arbitrary<Tuple2<Map<String, Integer>, String>> mapsWithKeyAlreadyIn() {
		return Arbitraries.maps(
				Arbitraries.strings().ofMinLength(1),
				Arbitraries.integers()
		).ofMinSize(1).flatMap(map -> {
			Arbitrary<String> keys = Arbitraries.of(new ArrayList<>(map.keySet()));
			return keys.map(key -> Tuple.of(map, key));
		});
	}

}
