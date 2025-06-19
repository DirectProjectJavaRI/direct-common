

package org.nhindirect.common.rest;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.exceptions.ServiceException;

public abstract class SecuredServiceRequestBase<T, E extends Exception> extends UnsecuredServiceRequestBase<T, E> {
    protected final ServiceSecurityManager securityManager;

    public SecuredServiceRequestBase(HttpClient httpClient, String serviceUrl, ObjectMapper jsonMapper, ServiceSecurityManager securityManager) {
        super(httpClient, serviceUrl, jsonMapper);
        if (securityManager == null) {
            throw new IllegalArgumentException("Security manager cannot be null");
        } else {
            this.securityManager = securityManager;
        }
    }

    public T call() throws E, IOException, ServiceException {
        HttpUriRequest request = this.createRequest();

        assert request != null;

        request = this.securityManager.createAuthenticatedRequest(request);
        HttpResponse response = this.httpClient.execute(request);

        Object var4;
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            var4 = this.interpretResponse(statusCode, response);
        } finally {
            closeConnection(response);
        }

        return (T)var4;
    }
}
