package pbt.spotify;

import java.util.*;

public class Song {

	// Not null
	private final String name;

	// One or more
	private final Set<Artist> artists;

	// Exactly one
	private final Album album;

	public Song(final String name, final Set<Artist> artists, final Album album) {
		this.name = name;
		this.artists = artists;
		this.album = album;
	}
}
