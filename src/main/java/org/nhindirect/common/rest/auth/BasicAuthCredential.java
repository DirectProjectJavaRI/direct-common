package org.nhindirect.common.rest.auth;

public interface BasicAuthCredential {
    String getUser();

    String getPassword();

    String getRole();
}
