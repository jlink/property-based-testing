package pbt.patterns;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Set;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

class InverseFunctions {

	@Property
	void encodeAndDecodeAreInverse( //
			@ForAll @StringLength(min = 1, max = 20) String toEncode, //
			@ForAll("charset") String charset //
	) throws UnsupportedEncodingException {
		String encoded = URLEncoder.encode(toEncode, charset);
		assertThat(URLDecoder.decode(encoded, charset)).isEqualTo(toEncode);
	}

	@Provide
	Arbitrary<String> charset() {
		Set<String> charsets = Charset.availableCharsets().keySet();
		return Arbitraries.of(charsets.toArray(new String[charsets.size()]));
	}
}
