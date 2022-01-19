package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.statistics.*;

import static org.assertj.core.api.Assertions.*;

@Domain(SpotifyDomain.class)
class SpotifyProperties {

	@Property(edgeCases = EdgeCasesMode.MIXIN)
	@StatisticsReport(format = NumberRangeHistogram.class)
	void statistics(@ForAll Spotify spotify) {
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

	@Property(edgeCases = EdgeCasesMode.MIXIN, afterFailure = AfterFailureMode.SAMPLE_FIRST, shrinking = ShrinkingMode.FULL)
	@StatisticsReport(format = NumberRangeHistogram.class)
	void followingMaximumOneUser(@ForAll Spotify spotify) {
		assertThat(spotify.users).allMatch(user -> user.following.size() <= 1);
	}

	@Property(tries = 10, edgeCases = EdgeCasesMode.FIRST)
	@Report(Reporting.GENERATED)
	void show(@ForAll Spotify spotify) {
	}

}
