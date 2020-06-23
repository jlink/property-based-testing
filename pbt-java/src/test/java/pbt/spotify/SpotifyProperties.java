package pbt.spotify;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.statistics.*;

class SpotifyProperties {

	@Property(tries = 1000, edgeCases = EdgeCasesMode.MIXIN, afterFailure = AfterFailureMode.RANDOM_SEED)
	// @Report(Reporting.GENERATED)
	@StatisticsReport(format = NumberRangeHistogram.class)
	void checkArbitraries(@ForAll("spotify") Tuple4<Set<Artist>, Set<Album>, Set<Song>, Set<User>> spotify) {
		Statistics.label("artists").collect(spotify.get1().size());
		Statistics.label("albums").collect(spotify.get2().size());
		Statistics.label("songs").collect(spotify.get3().size());
		Statistics.label("users").collect(spotify.get4().size());
		// System.out.println(spotify);

		Set<User> users = spotify.get4();
		// Statistics.label("following")
		// 		  .collect(users.stream().mapToInt(user -> user.following.size()).max().orElse(0));
		//Assertions.assertThat(users).allMatch(user -> user.following.isEmpty());
	}

	@Provide
	Arbitrary<Tuple4<Set<Artist>, Set<Album>, Set<Song>, Set<User>>> spotify() {
		return new SpotifyArbitraries().spotify();
	}
}
