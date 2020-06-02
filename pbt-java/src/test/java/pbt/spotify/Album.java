package pbt.spotify;

import java.util.*;

public class Album {

	// Not null
	private final String name;

	// One or more
	private final Set<Artist> artists;

	public Album(final String name, final Set<Artist> artists) {
		this.name = name;
		this.artists = artists;
	}
}
