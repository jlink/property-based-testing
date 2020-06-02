package pbt.spotify;

import java.util.*;

public class Song {

	// Not null
	public final String name;

	// One or more
	public final Set<Artist> artists;

	// Exactly one
	public final Album album;

	public Song(final String name, final Set<Artist> artists, final Album album) {
		this.name = name;
		this.artists = Collections.unmodifiableSet(artists);
		this.album = album;
	}
}
