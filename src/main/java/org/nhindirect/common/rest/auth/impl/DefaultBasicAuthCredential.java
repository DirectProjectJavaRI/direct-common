

package org.nhindirect.common.rest.auth.impl;

import org.nhindirect.common.rest.auth.BasicAuthCredential;

public class DefaultBasicAuthCredential implements BasicAuthCredential {
    protected final String name;
    protected final String password;
    protected final String role;

    public DefaultBasicAuthCredential(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getUser() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRole() {
        return this.role;
    }
}
