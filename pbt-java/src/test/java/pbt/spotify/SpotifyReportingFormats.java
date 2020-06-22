package pbt.spotify;

import java.util.*;

import net.jqwik.api.*;

public interface SpotifyReportingFormats {

	class Albums implements SampleReportingFormat {

		@Override
		public boolean appliesTo(final Object value) {
			return value instanceof Album;
		}

		@Override
		public Object report(final Object value) {
			Album album = (Album) value;
			Map<String, Object> map = new HashMap<>();
			map.put("name", album.name);
			map.put("artists", album.artists);
			return map;
		}

		@Override
		public Optional<String> label(final Object value) {
			return Optional.of("Album ");
		}
	}

	class Artists implements SampleReportingFormat {

		@Override
		public boolean appliesTo(final Object value) {
			return value instanceof Artist;
		}

		@Override
		public Object report(final Object value) {
			Artist artist = (Artist) value;
			Map<String, Object> map = new HashMap<>();
			map.put("name", artist.name);
			return map;
		}

		@Override
		public Optional<String> label(final Object value) {
			return Optional.of("Artist ");
		}
	}

	class Songs implements SampleReportingFormat {

		@Override
		public boolean appliesTo(final Object value) {
			return value instanceof Song;
		}

		@Override
		public Object report(final Object value) {
			Song song = (Song) value;
			Map<String, Object> map = new HashMap<>();
			map.put("name", song.name);
			map.put("album", song.album);
			map.put("artists", song.artists);
			return map;
		}

		@Override
		public Optional<String> label(final Object value) {
			return Optional.of("Song ");
		}
	}
}
