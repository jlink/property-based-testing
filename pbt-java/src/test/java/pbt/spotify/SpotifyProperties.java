package pbt.spotify;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

class SpotifyProperties {

	@Property(edgeCases = EdgeCasesMode.MIXIN)
	@StatisticsReport(format = NumberRangeHistogram.class)
	void statistics(@ForAll("spotify") Spotify spotify) {
		Statistics.label("artists").collect(spotify.artists.size());
		Statistics.label("albums").collect(spotify.albums.size());
		Statistics.label("songs").collect(spotify.songs.size());
		Statistics.label("users").collect(spotify.users.size());

		Set<User> users = spotify.users;
		Statistics.label("users following")
				  .collect(users.stream().mapToInt(user -> user.following.size()).max().orElse(0));
		Statistics.label("users liked")
				  .collect(users.stream().mapToInt(user -> user.liked.size()).max().orElse(0));
	}

	@Property(edgeCases = EdgeCasesMode.MIXIN, afterFailure = AfterFailureMode.RANDOM_SEED)
	@StatisticsReport(format = NumberRangeHistogram.class)
	void followingMaximumOneUser(@ForAll("spotify") Spotify spotify) {
		Assertions.assertThat(spotify.users).allMatch(user -> user.following.size() <= 1);
	}

	@Provide
	Arbitrary<Spotify> spotify() {
		return new SpotifyArbitraries().spotify();
	}
}
