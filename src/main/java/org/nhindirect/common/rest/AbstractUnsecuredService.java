

package org.nhindirect.common.rest;

import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.nhindirect.common.rest.exceptions.ServiceException;

public abstract class AbstractUnsecuredService {
    protected final String serviceURL;
    protected final HttpClient httpClient;
    protected final ObjectMapper jsonMapper;

    public AbstractUnsecuredService(String serviceURL, HttpClient httpClient) {
        if (httpClient != null && serviceURL != null && !serviceURL.isEmpty()) {
            this.httpClient = httpClient;
            this.serviceURL = serviceURL.endsWith("/") ? serviceURL : serviceURL + "/";
            this.jsonMapper = new ObjectMapper();
            this.jsonMapper.configure(Feature.WRITE_DATES_AS_TIMESTAMPS, false);
            this.jsonMapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        } else {
            throw new IllegalArgumentException("Invalid parameter received. Got: serviceURL: " + serviceURL + ", httpClient: " + httpClient);
        }
    }

    protected static String showSensitive(Object o) {
        int charsToShow = 4;
        if (o == null) {
            return "null";
        } else {
            String s = o.toString();
            int len = s.length();
            return len <= 4 ? s : s.substring(0, len - 4).replaceAll(".", "*") + s.substring(len - 4);
        }
    }

    protected <T, E extends Exception> T callWithRetry(ServiceRequest<T, E> request) throws E, ServiceException {
        Object e;
        try {
            e = request.call();
        } catch (IOException ioException) {
            throw new ServiceException(ioException);
        } finally {
            request.destroy();
        }

        return (T)e;
    }
}
