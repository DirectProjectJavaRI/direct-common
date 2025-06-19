

package org.nhindirect.common.rest;

import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.nhindirect.common.rest.exceptions.AuthException;
import org.nhindirect.common.rest.exceptions.ServiceException;

public abstract class AbstractSecuredService extends AbstractUnsecuredService {
    protected final ServiceSecurityManager securityManager;

    public AbstractSecuredService(String serviceURL, HttpClient httpClient, ServiceSecurityManager securityManager) {
        super(serviceURL, httpClient);
        if (securityManager == null) {
            throw new IllegalArgumentException("Security manager cannot be null");
        } else {
            this.securityManager = securityManager;
        }
    }

    protected <T, E extends Exception> T callWithRetry(ServiceRequest<T, E> request) throws E, ServiceException {
        Object e;
        try {
            e = (new AuthRetryRequest(request)).call();
        } catch (Exception exception) {
            throw new ServiceException(exception);
        } finally {
            request.destroy();
        }

        return (T)e;
    }

    private class AuthRetryRequest<T, E extends Exception> implements ServiceRequest<T, E> {
        private final ServiceRequest<T, E> request;

        public AuthRetryRequest(ServiceRequest<T, E> other) {
            this.request = other;
        }

        public T call() throws E, IOException, ServiceException {
            int retries = 1;

            while(true) {
                try {
                    return (T)this.request.call();
                } catch (AuthException e) {
                    if (retries-- <= 0) {
                        throw e;
                    }

                    AbstractSecuredService.this.securityManager.authenticateSession();
                }
            }
        }

        public void destroy() {
            this.request.destroy();
        }
    }
}
