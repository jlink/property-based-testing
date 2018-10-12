package pbt.web;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import okhttp3.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Testing the endpoint:  http://validate.jsontest.com
 * <p>
 * See http://www.jsontest.com/
 */
@Label("http://validate.jsontest.com")
class JsontestProperties implements AutoCloseable {

	private final String BASE_URL = "http://validate.jsontest.com";

	private final OkHttpClient client;

	public JsontestProperties() {
		this.client =
				new OkHttpClient()
						.newBuilder()
						.connectTimeout(5, TimeUnit.SECONDS)
						.readTimeout(5, TimeUnit.SECONDS)
						.writeTimeout(5, TimeUnit.SECONDS)
						.build();

	}

	@Property(tries = 10)
	@Label("arrays can be validated")
	void validateArrays(@ForAll @JsonArray String json) throws IOException {
		List originalList = toList(json);
		// prettyPrint(originalList);

		try (Response response = callValidate(json)) {
			assertThat(response.code()).isEqualTo(200);

			Map responseMap = toMap(response.body().string());
			assertThat(responseMap.get("validate")).isEqualTo(true);
			assertThat(responseMap.get("object_or_array")).isEqualTo("array");
			assertThat(responseMap.get("size")).isEqualTo(originalList.size());
		}
	}

	@Property(tries = 10)
	@Label("objects can be validated")
	void validateObjects(@ForAll @JsonObject String json) throws IOException {
		Map originalObject = toMap(json);
		// prettyPrint(originalObject);

		try (Response response = callValidate(json)) {
			assertThat(response.code()).isEqualTo(200);

			Map responseMap = toMap(response.body().string());
			assertThat(responseMap.get("validate")).isEqualTo(true);
			assertThat(responseMap.get("object_or_array")).isEqualTo("object");
			assertThat(responseMap.get("size")).isEqualTo(originalObject.size());
		}
	}

	@Example
	@Label("endpoint is available")
	void endpointAnswers() throws IOException {

		try (Response response = callValidate("{a:1}")) {
			assertThat(response.code()).isEqualTo(200);

			Map map = toMap(response.body().string());
			assertThat(map.get("validate")).isEqualTo(true);
			assertThat(map.get("object_or_array")).isEqualTo("object");
			assertThat(map.get("empty")).isEqualTo(false);
			assertThat(map.get("size")).isEqualTo(1);
		}
	}

	private Map toMap(String json) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, Map.class);
	}

	private List toList(String json) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, List.class);
	}

	private Response callValidate(String json) throws IOException {
		String url = BASE_URL + "?json=" + json;

		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		return client.newCall(request).execute();
	}

	private void prettyPrint(Object jsonObject) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		System.out.println(pretty);
	}

	@Override
	public void close() throws Exception {

	}
}
