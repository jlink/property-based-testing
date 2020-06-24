package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;

class SpotifyArbitraries {

	Arbitrary<Spotify> spotify() {
		return artists().set().ofMinSize(1)
						.flatMap(artists -> {
							return albums(artists).set().ofMinSize(1).flatMap(albums -> {
								return songs(albums).set().flatMap(songs -> {
									return users(songs).set().ofMinSize(1).map(users -> {
										return new Spotify(artists, albums, songs, users);
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

	Arbitrary<User> users(Set<Song> songs) {
		Arbitrary<Set<Song>> liked = Arbitraries.of(songs).set();
		Arbitrary<String> userName = uniqueNames();
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
}
