package org.nhindirect.common.rest;

import java.io.IOException;
import java.util.concurrent.Callable;
import org.nhindirect.common.rest.exceptions.ServiceException;

public interface ServiceRequest<T, E extends Exception> extends Callable<T> {
    T call() throws E, IOException, ServiceException;

    void destroy();
}
