

package org.nhindirect.common.rest.auth.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.nhindirect.common.crypto.exceptions.CryptoException;
import org.nhindirect.common.rest.auth.BasicAuthCredential;
import org.nhindirect.common.rest.auth.BasicAuthCredentialStore;
import org.nhindirect.common.rest.auth.NHINDPrincipal;
import org.nhindirect.common.rest.auth.exceptions.BasicAuthException;
import org.nhindirect.common.rest.auth.exceptions.NoSuchUserException;

public class HashableBasicAuthValidator extends AbstractBasicAuthValidator {
    protected final char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final String HASH_CLEAR = "Clear";
    public static final String HASH_MD5 = "MD5";
    public static final String HASH_SHA1 = "SHA1";
    public static final String HASH_SHA256 = "SHA256";
    public static final String HASH_SHA512 = "SHA512";
    protected static final Map<String, String> DIGEST_TYPE_MAP = new HashMap();
    protected String hashType = "Clear";

    public HashableBasicAuthValidator() {
    }

    public HashableBasicAuthValidator(BasicAuthCredentialStore credStore) {
        super(credStore);
        this.credStore = credStore;
    }

    public HashableBasicAuthValidator(BasicAuthCredentialStore credStore, String hashType) {
        super(credStore);
        this.setHashType(hashType);
    }

    public void setHashType(String hashType) {
        if (DIGEST_TYPE_MAP.get(hashType) == null) {
            throw new IllegalArgumentException("Unknown hash type " + hashType);
        } else {
            this.hashType = hashType;
        }
    }

    public NHINDPrincipal authenticate(String subject, String password) throws BasicAuthException {
        BasicAuthCredential cred = this.credStore.getCredential(subject);
        if (cred == null) {
            throw new NoSuchUserException();
        } else {
            try {
                String hashPass = this.convertPassToHash(password);
                if (hashPass.compareTo(cred.getPassword()) != 0) {
                    throw new BasicAuthException("Invalid credentials.");
                } else {
                    return new NHINDPrincipal(cred.getUser(), cred.getRole());
                }
            } catch (CryptoException e) {
                throw new BasicAuthException("Failed to validate password.", e);
            }
        }
    }

    protected String convertPassToHash(String password) throws CryptoException {
        if (this.hashType.compareToIgnoreCase("Clear") == 0) {
            return password;
        } else {
            String digistAlg = (String)DIGEST_TYPE_MAP.get(this.hashType);

            try {
                MessageDigest md = MessageDigest.getInstance(digistAlg);
                md.update(password.getBytes());
                byte[] digest = md.digest();
                return this.createStringRep(digest);
            } catch (NoSuchAlgorithmException e) {
                throw new CryptoException("Algorithm not supported.", e);
            }
        }
    }

    private String createStringRep(byte[] digest) {
        StringBuffer buf = new StringBuffer(digest.length * 2);

        for(byte bt : digest) {
            buf.append(this.hexDigits[(bt & 240) >> 4]);
            buf.append(this.hexDigits[bt & 15]);
        }

        return buf.toString();
    }

    static {
        DIGEST_TYPE_MAP.put("MD5", "MD5");
        DIGEST_TYPE_MAP.put("SHA1", "SHA-1");
        DIGEST_TYPE_MAP.put("SHA256", "SHA-256");
        DIGEST_TYPE_MAP.put("SHA512", "SHA-512");
    }
}
