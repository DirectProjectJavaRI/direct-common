package org.nhindirect.common.rest;

import org.apache.http.client.methods.HttpUriRequest;

public interface ServiceSecurityManager {
    void init();

    void authenticateSession();

    HttpUriRequest createAuthenticatedRequest(HttpUriRequest var1);
}
