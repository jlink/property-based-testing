package pbt.spotify;

public class Artist {

	// Not null
	public final String name;

	public Artist(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("Artist(%s)", name);
	}

}
