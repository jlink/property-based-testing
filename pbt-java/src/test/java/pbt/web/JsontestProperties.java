package pbt.web;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.databind.*;
import okhttp3.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Testing the endpoint:  http://validate.jsontest.com
 * <p>
 * See http://www.jsontest.com/
 */
class JsontestProperties {

	private final String BASE_URL = "http://validate.jsontest.com";

	private OkHttpClient client = new OkHttpClient();

	@Example
	void validateEndpoint() throws IOException {

		Response response = callValidate("{a:1}");
		assertThat(response.code()).isEqualTo(200);

		Map map = toMap(response.body().string());

		// System.out.println(map);
		assertThat(map.get("validate")).isEqualTo(true);
		assertThat(map.get("object_or_array")).isEqualTo("object");
		assertThat(map.get("empty")).isEqualTo(false);
		assertThat(map.get("size")).isEqualTo(1);
	}

	private Map toMap(String json) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, Map.class);
	}

	private Response callValidate(String json) throws IOException {
		String url = BASE_URL + "?json=" + json;

		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		return client.newCall(request).execute();
	}
}
