package pbt.spotify;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.statistics.*;

class SpotifyProperties {

	@Property(tries = 2, edgeCases = EdgeCasesMode.MIXIN, afterFailure = AfterFailureMode.RANDOM_SEED)
	@Report(Reporting.GENERATED)
	@StatisticsReport(format = NumberRangeHistogram.class)
	void checkArbitraries(@ForAll("spotify") Spotify spotify) {
		// Statistics.label("artists").collect(spotify.artists.size());
		// Statistics.label("albums").collect(spotify.albums.size());
		// Statistics.label("songs").collect(spotify.songs.size());
		// Statistics.label("users").collect(spotify.users.size());

		Set<User> users = spotify.users;
		Statistics.label("following")
				  .collect(users.stream().mapToInt(user -> user.following.size()).max().orElse(0));

		Assertions.assertThat(users).allMatch(user -> user.liked.isEmpty());
		// Assertions.assertThat(users).allMatch(user -> user.following.isEmpty());
	}

	@Provide
	Arbitrary<Spotify> spotify() {
		return new SpotifyArbitraries().spotify();
	}
}
