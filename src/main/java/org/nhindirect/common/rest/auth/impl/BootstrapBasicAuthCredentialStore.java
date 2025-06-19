

package org.nhindirect.common.rest.auth.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.nhindirect.common.rest.auth.BasicAuthCredential;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;

public class BootstrapBasicAuthCredentialStore implements BasicAuthCredentialStore {
    protected Map<String, BasicAuthCredential> credentialMap;

    public BootstrapBasicAuthCredentialStore() {
        this.credentialMap = new HashMap();
    }

    public BootstrapBasicAuthCredentialStore(List<BasicAuthCredential> credentials) {
        this();
        this.setCredentails(credentials);
    }

    public void setCredentails(List<BasicAuthCredential> credentials) {
        for(BasicAuthCredential cred : credentials) {
            this.credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
        }

    }

    public void setCredentialsAsDelimetedString(List<String> credentials) {
        for(String str : credentials) {
            String[] parsedStr = str.split(",");
            BasicAuthCredential cred = new DefaultBasicAuthCredential(parsedStr[0], parsedStr[1], parsedStr[2]);
            this.credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
        }

    }

    public void setCredentialsAsProperties(Properties credentials) {
        for(Map.Entry<Object, Object> entry : credentials.entrySet()) {
            String[] parsedStr = entry.getValue().toString().split(",");
            BasicAuthCredential cred = new DefaultBasicAuthCredential(parsedStr[0], parsedStr[1], parsedStr[2]);
            this.credentialMap.put(cred.getUser().toUpperCase(Locale.getDefault()), cred);
        }

    }

    public BasicAuthCredential getCredential(String name) {
        return (BasicAuthCredential)this.credentialMap.get(name.toUpperCase(Locale.getDefault()));
    }
}
