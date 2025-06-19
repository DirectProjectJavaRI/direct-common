package org.nhindirect.common.rest.auth;

import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;

public interface BasicAuthValidator {
    NHINDPrincipal authenticate(String var1) throws BasicAuthException;

    NHINDPrincipal authenticate(String var1, String var2) throws BasicAuthException;
}
