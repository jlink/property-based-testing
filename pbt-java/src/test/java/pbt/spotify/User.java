package pbt.spotify;

import java.util.*;

public class User {

	public final String name;

	public Set<Song> liked = new HashSet<>();

	public Set<User> following = new HashSet<>();

	public User(final String name) {
		this.name = name;
	}

	public void likeSong(final Song song) {
		liked.add(song);
	}

	public void followUser(final User user) {
		if (user.equals(this)) {
			throw new IllegalArgumentException("Users cannot follow themselves");
		}
		following.add(user);
	}
}
