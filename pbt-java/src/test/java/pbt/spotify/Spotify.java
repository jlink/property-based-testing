package pbt.spotify;

import java.util.*;

class Spotify {
	public final Set<Artist> artists;
	public final Set<Album> albums;
	public final Set<Song> songs;
	public final Set<User> users;

	public Spotify(final Set<Artist> artists, final Set<Album> albums, final Set<Song> songs, final Set<User> users) {
		this.artists = artists;
		this.albums = albums;
		this.songs = songs;
		this.users = users;
	}

}
