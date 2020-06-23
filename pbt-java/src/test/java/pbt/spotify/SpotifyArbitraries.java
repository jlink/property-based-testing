package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

class SpotifyArbitraries {

	Arbitrary<Tuple4<Set<Artist>, Set<Album>, Set<Song>, Set<User>>> spotify() {
		return artists().set().ofMinSize(1)
						.flatMap(artists -> {
							return albums(artists).set().flatMap(albums -> {
								return songs(albums).set().flatMap(songs -> {
									return users(songs).set().map(users -> {
										System.out.println("Users: " + users + ", ");
										Arbitrary<Set<User>> followees = Arbitraries.of(users).set();
										users.forEach(user -> {
											try {
												// To get rid of preshrinking followees since these are not shrunk away
												user.following.clear();
												// TODO: Those followees are not shrunk away!
												Set<User> sample = followees.sample();
												System.out.println("  Sample: " + sample);
												sample.forEach(user::follow);
												System.out.println("  Followees: " + user.following);
											} catch (IllegalArgumentException ignore) { }
										});
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
