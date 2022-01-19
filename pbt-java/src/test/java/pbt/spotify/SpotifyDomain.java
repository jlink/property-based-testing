package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.domains.*;

import static java.util.Collections.*;

import static net.jqwik.api.Arbitraries.*;

class SpotifyDomain extends DomainContextBase {

	@Provide
	Arbitrary<Spotify> spotify(@ForAll Set<Artist> artists) {
		return aSetOfAlbums(artists).flatMap(
			albums -> aSetOfSongs(albums).flatMap(
				songs -> aSetOfUsers(songs).map(
					users -> new Spotify(artists, albums, songs, users)
				)
			)
		);
	}

	@Provide
	Arbitrary<Set<Artist>> aSetOfArtists(@ForAll Arbitrary<Artist> anArtist) {
		return anArtist.set().uniqueElements(a -> a.name);
	}

	@Provide
	private Arbitrary<Artist> anArtist() {
		return names().map(Artist::new);
	}

	private Arbitrary<Song> aSong(Set<Album> albums) {
		Arbitrary<String> songName = names();
		Arbitrary<Album> album = Arbitraries.of(albums);
		Arbitrary<Set<Artist>> artists = album.flatMap(
			a -> Arbitraries.subsetOf(a.artists).ofMinSize(1)
		);
		return Combinators.combine(songName, album, artists)
						  .as((n, al, as) -> new Song(n, as, al));
	}

	private Arbitrary<Set<Song>> aSetOfSongs(final Set<Album> albums) {
		if (albums.isEmpty()) {
			return just(emptySet());
		}
		return aSong(albums).set().uniqueElements(s -> s.name);
	}

	private Arbitrary<Album> anAlbum(Set<Artist> artists) {
		Arbitrary<String> albumName = names();
		Arbitrary<Set<Artist>> albumArtists = Arbitraries.subsetOf(artists).ofMinSize(1);
		return Combinators.combine(albumName, albumArtists).as(Album::new);
	}

	private Arbitrary<Set<Album>> aSetOfAlbums(final Set<Artist> artists) {
		if (artists.isEmpty()) {
			return just(emptySet());
		}
		return anAlbum(artists).set().uniqueElements(a -> a.name);
	}

	private Arbitrary<Set<User>> aSetOfUsers(final Set<Song> songs) {
		SetArbitrary<User> aSetOfUsers = aUser(songs).set().uniqueElements(u -> u.name).ofMinSize(1);
		return addFollowingsToUsers(aSetOfUsers);
	}

	private Arbitrary<Set<User>> addFollowingsToUsers(final SetArbitrary<User> aSetOfUsers) {
		return aSetOfUsers.flatMapEach((allUsers, user) -> {
			return Arbitraries.of(allUsers).set().map(followees -> {
				followees.forEach(followee -> {
					try {
						user.follow(followee);
					} catch (IllegalArgumentException ignore) {}
				});
				return user;
			});
		});
	}

	private Arbitrary<User> aUser(Set<Song> songs) {
		Arbitrary<Set<Song>> liked = Arbitraries.subsetOf(songs);
		Arbitrary<String> userName = names();
		return Combinators
			.combine(userName, liked)
			.as((name, likedSongs) ->
				{
					User user = new User(name);
					for (Song likedSong : likedSongs) {
						user.like(likedSong);
					}
					return user;
				});
	}

	private Arbitrary<String> names() {
		return Arbitraries.strings().withCharRange('a', 'z').ofLength(3);
	}

}
