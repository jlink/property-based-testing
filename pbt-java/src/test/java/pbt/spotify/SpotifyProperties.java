package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;

class SpotifyProperties {

	@Property(tries = 10)
	void checkArbitraries(@ForAll("spotify")Tuple.Tuple3<Set<Artist>, Set<Album>, Set<Song>> spotify) {
		System.out.println(spotify);
	}

	@Provide
	Arbitrary<Tuple.Tuple3<Set<Artist>, Set<Album>, Set<Song>>> spotify() {
		return new SpotifyArbitraries().spotify();
	}
}
