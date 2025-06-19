

package org.nhindirect.common.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.nhindirect.common.rest.exceptions.AuthorizationException;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.common.rest.exceptions.ServiceMethodException;

public abstract class UnsecuredServiceRequestBase<T, E extends Exception> implements ServiceRequest<T, E> {
    protected final ObjectMapper jsonMapper;
    protected final HttpClient httpClient;
    protected final String serviceUrl;

    protected UnsecuredServiceRequestBase(HttpClient httpClient, String serviceUrl, ObjectMapper jsonMapper) {
        this.httpClient = httpClient;
        this.jsonMapper = jsonMapper;
        this.serviceUrl = serviceUrl;
    }

    public T call() throws E, IOException, ServiceException {
        HttpUriRequest request = this.createRequest();
        if (request == null) {
            throw new ServiceException("Could not create request object");
        } else {
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

    protected T interpretResponse(int statusCode, HttpResponse response) throws IOException, E, ServiceException {
        switch (statusCode) {
            case 200:
            case 201:
            case 204:
                return (T)this.parseResponse(response.getEntity());
            case 401:
                throw this.handleUnauthorized(response);
            case 404:
                throw new ServiceMethodException(404, "Failed to locate target service. Is '" + this.serviceUrl + "' the correct URL?");
            default:
                throw unexpectedStatus(statusCode, response.getEntity());
        }
    }

    protected abstract HttpUriRequest createRequest() throws IOException;

    protected abstract T parseResponse(HttpEntity var1) throws IOException;

    protected static final String uriEscape(String val) throws ServiceException {
        try {
            String escapedVal = URLEncoder.encode(val, "UTF-8");
            return escapedVal.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Failed to encode value: " + val, e);
        }
    }

    protected static final ServiceMethodException unexpectedStatus(int statusCode, HttpEntity responseEntity) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (responseEntity != null) {
            responseEntity.writeTo(out);
        }

        return new ServiceMethodException(statusCode, "Unexpected HTTP status code received from target service: " + statusCode + ". Response body contained: " + out.toString("UTF-8"));
    }

    protected static final void closeConnection(HttpResponse response) throws IOException {
        HttpEntity e = response.getEntity();
        if (e != null) {
            e.getContent().close();
        }

    }

    protected final <R extends HttpEntityEnclosingRequest> R buildEntityRequest(R request, byte[] contents, String contentType) {
        return (R)this.buildEntityRequest(request, contents, contentType, "UTF-8");
    }

    protected final <R extends HttpEntityEnclosingRequest> R buildEntityRequest(R request, byte[] contents, String contentType, String contentEncoding) {
        ByteArrayEntity entity = new ByteArrayEntity(contents);
        entity.setContentType(contentType);
        entity.setContentEncoding(contentEncoding);
        request.setEntity(entity);
        return request;
    }

    protected final AuthorizationException handleUnauthorized(HttpResponse response) {
        return new AuthorizationException("Action not authorized");
    }

    protected final void checkContentType(String expected, HttpEntity entity) throws ServiceException {
        try {
            if (!entity.getContentType().getValue().contains(expected)) {
                throw this.incompatibleClientException();
            }
        } catch (NullPointerException var4) {
            throw this.incompatibleClientException();
        }
    }

    protected final ServiceException incompatibleClientException() {
        return new ServiceException("This version of target service is incompatible with the server located at " + this.serviceUrl + ".");
    }

    public void destroy() {
    }
}
