package pbt.spotify;

import java.util.*;

public class User {

	public final String name;

	public final Set<Song> liked = new HashSet<>();

	public final Set<User> following = new HashSet<>();

	public User(final String name) {
		this.name = name;
	}

	public void like(final Song song) {
		liked.add(song);
	}

	public void follow(final User user) {
		if (user.equals(this)) {
			throw new IllegalArgumentException("Users cannot follow themselves");
		}
		following.add(user);
	}

	@Override
	public String toString() {
		return String.format("User(%s)", name);
	}
}
