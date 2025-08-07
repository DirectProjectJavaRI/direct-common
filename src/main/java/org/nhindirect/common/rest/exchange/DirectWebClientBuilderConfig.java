package org.nhindirect.common.rest.exchange;

import org.nhindirect.common.rest.exceptions.AuthorizationException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class DirectWebClientBuilderConfig {
	

	@Value("${direct.webservices.retry.backoff.multiplier:3}")
	protected double backoffMultiplier;
	
	@Value("${direct.webservices.retry.backoff.initialBackoffInterval:100}")
	protected long initialBackoffInterval;	
	
	@Value("${direct.webservices.retry.backoff.maxInterval:20000}")
	protected long maxInterval;		
	
	@Value("${direct.webservices.security.basic.user.name:}")
	protected String user;	
	
	@Value("${direct.webservices.security.basic.user.password:}")
	protected String pass;	

	@Bean
	public WebClient.Builder directWebClientBuilder() {
		
		// TODO: Add retry configuration
		
		WebClient.Builder builder = WebClient.builder();
		
		if (StringUtils.hasText(user))
			builder.filter(ExchangeFilterFunctions.basicAuthentication(user, pass));

		
		builder.defaultStatusHandler(HttpStatusCode::isError, resp -> decodeReactiveHttpError(resp));
		
		return builder;
		
	}
	
	
	private Mono<? extends Throwable> decodeReactiveHttpError(ClientResponse clientResponse) {
		

    	if (clientResponse.statusCode().value() == 401) {
    		
            return Mono.just(new AuthorizationException("Action not authorized"));
        } 
    	else if (clientResponse.statusCode().value() == 404) {

    		
            return Mono.just(new ServiceMethodException(404, ""));
        }    	
    	else {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody -> Mono.error(new ServiceMethodException(clientResponse.statusCode().value(),
	                "Unexpected HTTP status code received from target service: " + clientResponse.statusCode().value()
	                        + ". Response body contained: " + errorBody)));
    	}


		
	}
}
