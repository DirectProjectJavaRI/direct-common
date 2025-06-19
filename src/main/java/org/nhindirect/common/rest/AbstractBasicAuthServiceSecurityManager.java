
package org.nhindirect.common.rest;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpUriRequest;

public abstract class AbstractBasicAuthServiceSecurityManager implements ServiceSecurityManager {
    protected final String AUTH_TYPE = "BASIC ";
    protected final String AUTH_HEADER = "Authorization";
    protected String username;
    protected String password;

    public AbstractBasicAuthServiceSecurityManager() {
    }

    public void init() {
    }

    public void authenticateSession() {
    }

    public HttpUriRequest createAuthenticatedRequest(HttpUriRequest request) {
        String basicAuthCredFormat = this.username + ":" + this.password;
        String encodedFormat = "BASIC " + new String(Base64.encodeBase64(basicAuthCredFormat.getBytes()));
        request.addHeader("Authorization", encodedFormat);
        return request;
    }
}
