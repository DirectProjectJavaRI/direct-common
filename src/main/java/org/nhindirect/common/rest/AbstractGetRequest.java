
package org.nhindirect.common.rest;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.nhindirect.common.rest.exceptions.ServiceException;

public abstract class AbstractGetRequest<T> extends SecuredServiceRequestBase<Collection<T>, ServiceException> {
    protected final boolean collectionRequest;

    protected AbstractGetRequest(HttpClient httpClient, String serviceUrl, ObjectMapper jsonMapper, ServiceSecurityManager securityManager, boolean collectionRequest) {
        super(httpClient, serviceUrl, jsonMapper, securityManager);
        this.collectionRequest = collectionRequest;
    }

    protected abstract String getRequestUri() throws ServiceException;

    protected Collection<T> interpretResponse(int statusCode, HttpResponse response) throws IOException, ServiceException {
        switch (statusCode) {
            case 200:
                return (Collection)super.interpretResponse(statusCode, response);
            case 204:
            case 404:
                return Collections.emptyList();
            default:
                return (Collection)super.interpretResponse(statusCode, response);
        }
    }

    protected final HttpUriRequest createRequest() throws IOException {
        try {
            HttpGet get = new HttpGet(this.getRequestUri());
            return get;
        } catch (ServiceException e) {
            throw new IOException("Error creating request.", e);
        }
    }

    protected List<T> parseResponse(HttpEntity response) throws IOException {
        Class<T> persistentClass = (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        List<T> retVal;
        if (this.collectionRequest) {
            retVal = (List)this.jsonMapper.readValue(response.getContent(), TypeFactory.collectionType(ArrayList.class, persistentClass));
        } else {
            T single = (T)this.jsonMapper.readValue(response.getContent(), persistentClass);
            retVal = Arrays.asList(single);
        }

        return retVal;
    }
}
