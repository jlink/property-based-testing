package pbt.spotify;

import java.util.*;

import net.jqwik.api.stateful.*;

class Spotify {
	public final Set<Artist> artists;
	public final Set<Album> albums;
	public final Set<Song> songs;
	public final Set<User> users;
	public final ActionSequence<Spotify> effects;

	public Spotify(
			final Set<Artist> artists,
			final Set<Album> albums,
			final Set<Song> songs,
			final Set<User> users,
			final ActionSequence<Spotify> effects
	) {
		this.artists = artists;
		this.albums = albums;
		this.songs = songs;
		this.users = users;
		this.effects = effects;
	}

}
