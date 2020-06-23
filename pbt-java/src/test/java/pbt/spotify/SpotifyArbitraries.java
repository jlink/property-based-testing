package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;

class SpotifyArbitraries {

	Arbitrary<Tuple.Tuple4<Set<Artist>, Set<Album>, Set<Song>, Set<User>>> spotify() {
		return artists().set().ofMinSize(1)
						.flatMap(artists -> {
							return albums(artists).set().flatMap(albums -> {
								return songs(albums).set().flatMap(songs -> {
									return users().set().map(users -> {
										// Add users.following
										// Add users.liked
										return Tuple.of(artists, albums, songs, users);
									});
								});
							});
						});
	}

	Arbitrary<Artist> artists() {
		return uniqueNames().map(Artist::new);
	}

	private Arbitrary<String> uniqueNames() {
		return Arbitraries.strings().alpha().ofLength(5).unique();
	}

	Arbitrary<Album> albums(Set<Artist> artists) {
		Arbitrary<String> albumName = uniqueNames();
		Arbitrary<Set<Artist>> albumArtists = Arbitraries.of(artists).set().ofMinSize(1);
		return Combinators.combine(albumName, albumArtists).as(Album::new);
	}

	Arbitrary<Song> songs(Set<Album> albums) {
		Arbitrary<String> songName = uniqueNames();
		Arbitrary<Album> album = Arbitraries.of(albums);
		Arbitrary<Set<Artist>> artists = album.flatMap(a -> {
			return Arbitraries.of(a.artists).set().ofMinSize(1);
		});
		return Combinators.combine(songName, album, artists)
						  .as((n, al, as) -> new Song(n, as, al));
	}

	Arbitrary<User> users() {
		Arbitrary<String> userName = uniqueNames();
		return userName.map(User::new);
	}
}
