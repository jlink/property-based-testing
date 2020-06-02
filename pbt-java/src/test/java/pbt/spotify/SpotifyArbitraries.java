package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;

class SpotifyArbitraries {

	Arbitrary<Tuple.Tuple3<Set<Artist>, Set<Album>, Set<Song>>> spotify() {
		return artists().set().ofMinSize(1)
						.flatMap(artists -> {
							return albums(artists).set().flatMap(albums -> {
								return songs(albums).set().map(songs -> Tuple.of(artists, albums, songs));
							});
						});
	}

	Arbitrary<Artist> artists() {
		return uniqueNames().map(name -> new Artist("Artist-" + name));
	}

	private Arbitrary<String> uniqueNames() {
		return Arbitraries.strings().alpha().ofLength(5).unique();
	}

	Arbitrary<Album> albums(Set<Artist> artists) {
		Arbitrary<String> albumName = uniqueNames().map(name -> "Album-" + name);
		List<Artist> artistsList = new ArrayList<>(artists);
		Arbitrary<Set<Artist>> albumArtists = Arbitraries.of(artistsList).set().ofMinSize(1);
		return Combinators.combine(albumName, albumArtists).as(Album::new);
	}

	Arbitrary<Song> songs(Set<Album> albums) {
		Arbitrary<String> songName = uniqueNames().map(name -> "Song-" + name);
		List<Album> albumList = new ArrayList<>(albums);
		Arbitrary<Album> album = Arbitraries.of(albumList);
		Arbitrary<Set<Artist>> artists = album.flatMap(a -> {
			List<Artist> artistsList = new ArrayList<>(a.artists);
			return Arbitraries.of(artistsList).set().ofMinSize(1);
		});
		return Combinators.combine(songName, album, artists)
						  .as((n, al, as) -> new Song(n, as, al));
	}
}
