package pbt.spotify;

import java.util.*;

public class Album {

	// Not null
	public final String name;

	// One or more
	public final Set<Artist> artists;

	public Album(final String name, final Set<Artist> artists) {
		this.name = name;
		this.artists = Collections.unmodifiableSet(artists);
	}

	@Override
	public String toString() {
		return String.format("Album(%s)", name);
	}

}
