package pbt.spotify;

public class Artist {

	// Not null
	private final String name;

	public Artist(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(name);
		return sb.toString();
	}

}
