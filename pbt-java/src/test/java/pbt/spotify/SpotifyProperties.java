package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

class SpotifyProperties {

	@Property(tries = 10)
	@Report(Reporting.GENERATED)
	@StatisticsReport(format = NumberRangeHistogram.class)
	void checkArbitraries(@ForAll("spotify")Tuple.Tuple3<Set<Artist>, Set<Album>, Set<Song>> spotify) {
		Statistics.label("artists").collect(spotify.get1().size());
		Statistics.label("albums").collect(spotify.get2().size());
		Statistics.label("songs").collect(spotify.get3().size());
		// System.out.println(spotify);
	}

	@Provide
	Arbitrary<Tuple.Tuple3<Set<Artist>, Set<Album>, Set<Song>>> spotify() {
		return new SpotifyArbitraries().spotify();
	}
}
