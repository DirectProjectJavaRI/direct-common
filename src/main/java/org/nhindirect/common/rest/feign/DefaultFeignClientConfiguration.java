/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.common.rest.feign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

import feign.Retryer;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;

/**
 * Default feign client configuration.  Includes retry policies, basic auth user name and password, and HTTP status decoder.
 * @author Greg Meyer
 * @since 6.0
 */
public class DefaultFeignClientConfiguration
{	
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
	
	/**
	 * Creates an instance of the a the default HTTP status translator.
	 * @return An instance of the a the default HTTP status translator
	 */
	@Bean
	public ErrorDecoder feignClientErrorDecoder()
	{
		return new DefaultErrorDecoder();
	}
	
	/**
	 * Creates an instance of BasicAuth interceptor configured with a username and password.   This bean is only created if the
	 * "direct.webservices.security.basic.user.name" property is set.
	 * @return An instance of BasicAuth interceptor configured with a username and password
	 */
    @Bean
    @ConditionalOnProperty(name="direct.webservices.security.basic.user.name", matchIfMissing=false)
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() 
    {
        return new BasicAuthRequestInterceptor(user, pass);
    }	
    
    /**
     * Creates an instance of a back off policy used in conjuntion with the retry policy.
     * @return An instance of a back off policy
     */
    @Bean
    public LoadBalancedRetryFactory backOffPolciyFactory()
    {
    	return new LoadBalancedRetryFactory() 
    	{
            @Override
            public BackOffPolicy createBackOffPolicy(String service) 
            {
            	final ExponentialBackOffPolicy backoffPolicy = new ExponentialBackOffPolicy();
            	backoffPolicy.setMultiplier(backoffMultiplier);
            	backoffPolicy.setInitialInterval(initialBackoffInterval);
            	backoffPolicy.setMaxInterval(maxInterval);
            	
                return backoffPolicy;
            }
        };    	
    }
    
    /**
     * Creates a default http retry policy.
     * @return A default http retry policy.
     */ 
    @Bean
    public Retryer retryer() 
    {
    	/*
    	 * Default retryer config
    	 */
        return new Retryer.Default(200, 1000, 5);
    }
}
