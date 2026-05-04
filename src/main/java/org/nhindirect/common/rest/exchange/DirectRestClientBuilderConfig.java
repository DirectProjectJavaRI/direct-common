package org.nhindirect.common.rest.exchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.nhindirect.common.rest.exceptions.AuthorizationException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class DirectRestClientBuilderConfig {

	@Value("${direct.webservices.connect.timeout:5000}")
	protected int connectTimeoutMs;

	@Value("${direct.webservices.response.timeout:10000}")
	protected long responseTimeoutMs;

	@Value("${direct.webservices.security.basic.user.name:}")
	protected String user;

	@Value("${direct.webservices.security.basic.user.password:}")
	protected String pass;

	@Bean
	public RestClient.Builder directRestClientBuilder() {

		HttpClient httpClient = HttpClients.custom()
				.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
						.setMaxConnTotal(50)
						.setConnectionTimeToLive(TimeValue.ofMinutes(5))
						.build())
				.evictExpiredConnections()
				.evictIdleConnections(TimeValue.ofSeconds(30))
				.setDefaultRequestConfig(RequestConfig.custom()
						.setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
						.setResponseTimeout(Timeout.ofMilliseconds(responseTimeoutMs))
						.build())
				.build();

		RestClient.Builder builder = RestClient.builder()
				.requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
				.defaultStatusHandler(HttpStatusCode::isError, this::decodeHttpError);

		if (StringUtils.hasText(user)) {
			String credentials = Base64.getEncoder()
					.encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
			builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
		}

		return builder;
	}

	private void decodeHttpError(HttpRequest request, ClientHttpResponse response) throws IOException {
		int statusCode = response.getStatusCode().value();
		if (statusCode == 401) {
			throw new AuthorizationException("Action not authorized");
		} else if (statusCode == 404) {
			throw new ServiceMethodException(404, "");
		} else {
			String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
			throw new ServiceMethodException(statusCode,
					"Unexpected HTTP status code received from target service: " + statusCode
							+ ". Response body contained: " + body);
		}
	}
}
