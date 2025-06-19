package org.nhindirect.common.rest.auth.impl;

import org.apache.commons.codec.binary.Base64;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;
import org.nhindirect.common.rest.auth.BasicAuthValidator;
import org.nhindirect.common.rest.auth.NHINDPrincipal;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;

public abstract class AbstractBasicAuthValidator implements BasicAuthValidator {
    protected BasicAuthCredentialStore credStore;

    public AbstractBasicAuthValidator() {
    }

    public AbstractBasicAuthValidator(BasicAuthCredentialStore credStore) {
        this.setAuthStore(credStore);
    }

    public void setAuthStore(BasicAuthCredentialStore credStore) {
        this.credStore = credStore;
    }

    public NHINDPrincipal authenticate(String rawAuth) throws BasicAuthException {
        int idx = rawAuth.indexOf(" ");
        String parsedRawAuth = idx >= 0 ? rawAuth.substring(idx + 1) : rawAuth;
        String authString = new String(Base64.decodeBase64(parsedRawAuth));
        String[] userPass = authString.split(":");
        return this.authenticate(userPass[0], userPass[1]);
    }
}
