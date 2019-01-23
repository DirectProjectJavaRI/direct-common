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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.nhindirect.common.rest.exceptions.AuthorizationException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * Default feign configuration for translating HTTP error codes.
 * @author Greg Meyer
 * @since 6.0
 */
public class DefaultErrorDecoder implements ErrorDecoder
{

	@SuppressWarnings("deprecation")
	@Override
	/**
	 * {@inheritDoc}
	 */
	public Exception decode(String methodKey, Response response)
	{
		switch (response.status())
		{
			case 401:
				return new AuthorizationException("Action not authorized");
			case 404: 
			{
				final String reason = (response == null || response.reason() == null) ? "" : response.reason();
				
				return new ServiceMethodException(404, reason);
			}
			default:
			{
		        final ByteArrayOutputStream out = new ByteArrayOutputStream();
		        String body = "";
		        if (response.body() != null) 
		        {
		        	try
					{
						IOUtils.copy(response.body().asInputStream(), out);
						body = out.toString("UTF-8");
					} 
		        	catch (IOException e)
					{

					}
		        	finally
		        	{
		        		IOUtils.closeQuietly(out);
		        	}
		        }
		        return new ServiceMethodException(response.status(),
		                "Unexpected HTTP status code received from target service: " + response.status()
		                        + ". Response body contained: " + body);
			}

		}
	}

}
