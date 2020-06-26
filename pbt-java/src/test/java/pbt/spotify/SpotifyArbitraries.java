package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

class SpotifyArbitraries {

	Arbitrary<Spotify> spotify() {
		return aSetOfArtists()
					   .flatMap(artists -> {
						   return aSetOfAlbums(artists).flatMap(albums -> {
							   return aSetOfSongs(albums).flatMap(songs -> {
								   return aSetOfUsers(songs).map(users -> {
									   return new Spotify(artists, albums, songs, users);
								   });
							   });
						   });
					   });
	}

	private Arbitrary<Artist> anArtist() {
		return uniqueNames().map(Artist::new);
	}

	private SetArbitrary<Artist> aSetOfArtists() {
		return anArtist().set();
	}

	private Arbitrary<Song> aSong(Set<Album> albums) {
		Arbitrary<String> songName = uniqueNames();
		Arbitrary<Album> album = Arbitraries.of(albums);
		Arbitrary<Set<Artist>> artists = album.flatMap(a -> {
			return Arbitraries.of(a.artists).set().ofMinSize(1);
		});
		return Combinators.combine(songName, album, artists)
						  .as((n, al, as) -> new Song(n, as, al));
	}

	private SetArbitrary<Song> aSetOfSongs(final Set<Album> albums) {
		return aSong(albums).set();
	}

	private Arbitrary<Album> anAlbum(Set<Artist> artists) {
		Arbitrary<String> albumName = uniqueNames();
		Arbitrary<Set<Artist>> albumArtists = Arbitraries.of(artists).set().ofMinSize(1);
		return Combinators.combine(albumName, albumArtists).as(Album::new);
	}

	private SetArbitrary<Album> aSetOfAlbums(final Set<Artist> artists) {
		return anAlbum(artists).set();
	}

	private Arbitrary<Set<User>> aSetOfUsers(final Set<Song> songs) {
		SetArbitrary<User> aSetOfUsers = aUser(songs).set().ofMinSize(1);
		return addFollowingsToUsers(aSetOfUsers);
	}

	private Arbitrary<Set<User>> addFollowingsToUsers(final SetArbitrary<User> aSetOfUsers) {
		return aSetOfUsers.flatMapEach((allUsers, user) -> {
			// Necessary to remove old state during shrinking
			user.following.clear();
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

	private Arbitrary<String> uniqueNames() {
		return Arbitraries.strings().withCharRange('a', 'z').ofLength(3).unique();
	}

}
