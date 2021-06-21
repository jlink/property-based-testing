package pbt.graphs;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.statistics.*;

/**
 * Motivated by https://stackoverflow.com/questions/67995000/looking-for-better-ways-to-generate-a-list-of-edges-for-a-graph-in-jqwik-propert/68047919#68047919
 */
class StackoverflowQuestionExample {

	@Provide
	Arbitrary<Set<Tuple2<String, Set<Tuple2<String, Integer>>>>> nodes() {
		int maxVertices = 20;
		int degreeMax = 4;
		int minEdgeFlow = 1;
		int maxEdgeFlow = 10;

		Arbitrary<String> anyVertix = Arbitraries.strings().withCharRange('a', 'z').ofLength(3);
		SetArbitrary<String> anyVertices = anyVertix.set().ofMinSize(1).ofMaxSize(maxVertices);

		return anyVertices.flatMapEach((vertices, vertix) -> {

			// Single vertix is a special case
			if (vertices.size() <= 1) {
				return Arbitraries.just(Tuple.of(vertix, Collections.emptySet()));
			}

			Set<String> possibleTargetVertices = new HashSet<>(vertices);
			possibleTargetVertices.remove(vertix);

			Arbitrary<Integer> anyEdgeFlow = Arbitraries.integers().between(minEdgeFlow, maxEdgeFlow);
			Arbitrary<Tuple2<String, Integer>> anyConnection =
				Combinators.combine(Arbitraries.of(possibleTargetVertices), anyEdgeFlow).as(Tuple::of);

			SetArbitrary<Tuple2<String, Integer>> anyConnections = anyConnection.set().ofMaxSize(degreeMax);

			return anyConnections.map(connections -> Tuple.of(vertix, connections));
		});
	}

	@Property(tries = 100)
	@Report(Reporting.GENERATED)
	@StatisticsReport(label = "count nodes", format = NumberRangeHistogram.class)
	@StatisticsReport(label = "max degree", format = Histogram.class)
	void checkNodes(@ForAll("nodes") Set<Tuple2<String, Set<Tuple2<String, Integer>>>> nodes) {
		Statistics.label("count nodes").collect(nodes.size());

		int maxDegree = nodes.stream().mapToInt(node -> node.get2().size()).max().orElse(0);
		Statistics.label("max degree").collect(maxDegree);
	}

}
