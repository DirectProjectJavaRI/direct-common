/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.common.options;

import org.apache.commons.lang3.StringUtils;

/**
 * Tuning and configuration options for components of the security and trust agent.  Options can be set either programmatically, as JVM options, or from a properties file.
 * JVM and/or property based settings can be overridden by setting options programmatically.  
 * @author Greg Meyer
 * @since 1.4
 */
public class OptionsParameter 
{	
	/**
	 * String value that specifies the JCE provider that should be used for cryptography and certificate operations.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptography.JCEProviderName
	 */
	public final static String JCE_PROVIDER = "JCE_PROVIDER";

	/**
	 * String value that specifies the JCE provider that should be used for cryptography and certificate operations that require
	 * access to sensitive cryptographic information such as secret and private keys.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptography.JCESensitiveProviderName
	 */
	public final static String JCE_SENTITIVE_PROVIDER = "JCE_SENTITIVE_PROVIDER";
	
	/**
	 * String value that specifies a comma delimited list of JCE provider classes that should be registered.  Each
	 * class in the list should be fully qualified with the package name.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptography.JCEProviderClassNames
	 */
	public final static String JCE_PROVIDER_CLASSES = "JCE_PROVIDER_CLASSES";
	
	/**
	 * String value that specifies a comma delimited list of JCE provider classes that require
	 * access to sensitive cryptographic information such as secret and private keys.  Each
	 * class in the list should be fully qualified with the package name.  If the provider class requires additional
	 * qualifier information, it should be delimited by a semicolon.  Example using BouncyCastle and the SunPKCS11 provider:
	 * <br>
	 * <i>org.bouncycastle.jce.provider.BouncyCastleProvider,sun.security.pkcs11.SunPKCS11;pkcs11Config/pkcs11.cfg</i>
	 * 
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptography.JCESensitiveProviderClassNames
	 */
	public final static String JCE_SENSITIVE_PROVIDER_CLASSES = "JCE_SENSITIVE_PROVIDER_CLASSES";
	
	/**
	 * String value that specifies the directory where CRLs will be cached.  The directory may a full or relative path.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.CRLCacheLocation
	 */
	public final static String CRL_CACHE_LOCATION = "CRL_CACHE_LOCATION";
	
	/**
	 * String value that sets the servers that will be used for DNS cert resolution.  Be default
	 * the DNS resolver uses the machine's local DNS setting, but this allows for it to be overridden.  If multiple servers
	 * are configured, then they are delimited by a comma (,) character.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.dnsresolver.Servers
	 */
	public final static String DNS_CERT_RESOLVER_SERVERS = "DNS_CERT_RESOLVER_SERVERS";
	
	/**
	 * Integer value that specifies the number of times the DNS certificate resolvers will retry a query
	 * to the DNS server.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.dnsresolver.ServerRetries
	 */
	public final static String DNS_CERT_RESOLVER_RETRIES = "DNS_CERT_RESOLVER_RETRIES";
	
	/**
	 * Integer value that specifies the query timeout in seconds for a DNS record.
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.dnsresolver.ServerTimeout
	 */
    public final static String DNS_CERT_RESOLVER_TIMEOUT = "DNS_CERT_RESOLVER_TIMEOUT";
	
 	/**
 	 * Boolean value that specifies if the DNS server should use TCP connections for queries.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.dnsresolver.ServerUseTCP
 	 */
    public final static String DNS_CERT_RESOLVER_USE_TCP = "DNS_CERT_RESOLVER_USE_TCP"; 

 	/**
 	 * Integer value specifies the maximum number of certificates that can be held in the DNS certificate cache.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.dnsresolver.MaxCacheSize
 	 */
    public final static String DNS_CERT_RESOLVER_MAX_CACHE_SIZE = "DNS_CERT_RESOLVER_MAX_CACHE_SIZE";     
    
 	/**
 	 * Integer value specifies the time to live in seconds that a certificate can be held in the DNS certificate cache.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.dnsresolver.CacheTTL
 	 */
    public final static String DNS_CERT_RESOLVER_CACHE_TTL = "DNS_CERT_RESOLVER_CACHE_TTL"; 
    
 	/**
 	 * Integer value specifies the maximum number of certificates that can be held in the LDAP certificate cache.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.ldapresolver.MaxCacheSize
 	 */
    public final static String LDAP_CERT_RESOLVER_MAX_CACHE_SIZE = "LDAP_CERT_RESOLVER_MAX_CACHE_SIZE";     
    
 	/**
 	 * Integer value specifies the time to live in seconds that a certificate can be held in the LDAP certificate cache.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.ldapresolver.CacheTTL
 	 */
    public final static String LDAP_CERT_RESOLVER_CACHE_TTL = "LDAP_CERT_RESOLVER_CACHE_TTL"; 
    
 	/**
 	 * Integer value specifies the maximum number of certificates that can be held in the CacheablePKCS11CertificateStore cache.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.cacheablepkcs11certificatestore.MaxCacheSize
 	 */
    public final static String CACHABLE_PKCS11_CERT_RESOLVER_MAX_CACHE_SIZE = "CACHABLE_PKCS11_CERT_RESOLVER_MAX_CACHE_SIZE";     
    
 	/**
 	 * Integer value specifies the time to live in seconds that a certificate can be held in the CacheablePKCS11CertificateStore cache.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.cacheablepkcs11certificatestore.CacheTTL
 	 */
    public final static String CACHABLE_PKCS11_CERT_RESOLVER_CACHE_TTL = "CACHABLE_PKCS11_CERT_RESOLVER_CACHE_TTL";     
    
 	/**
 	 * Boolean value that indicates if strong digests should be enforced.  By spec, weak digest algorithms such as MD5 are not allowed; setting 
 	 * this parameter will force messages with weak message digests to be rejected.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptographer.smime.EnforceStrongDigests
 	 */
    public final static String ENFORCE_STRONG_DIGESTS = "ENFORCE_STRONG_DIGESTS";    
    
 	/**
 	 * Boolean value that indicates if strong encryption should be enforced.  By spec, weak encryption algorithms such as 3DES are not allowed; setting 
 	 * this parameter will force messages with weak message encryption to be rejected.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptographer.smime.EnforceStrongEncryption
 	 */
    public final static String ENFORCE_STRONG_ENCRYPTION = "ENFORCE_STRONG_ENCRYPTION";  
    
	/**
	 * String value that specifies the encryption algorithm used to encrypt messages by the SMIME cryptographer
	 * <br>Valid option values:
	 * <ul>
	 * <li>RSA_3DES</li>
	 * <li>AES128</li>
	 * <li>AES192</li>
	 * <li>AES256</li>
	 * </ul>
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm
	 */
    public final static String CRYPTOGRAHPER_SMIME_ENCRYPTION_ALGORITHM = "CRYPTOGRAHPER_SMIME_ENCRYPTION_ALGORITHM";

	/**
	 * String value that specifies the key encryption algorithm used to encrypt message keys by the SMIME cryptographer
	 * <br>Valid option values:
	 * <ul>
	 * <li>RSA_OAEP</li>
	 * <li>RSA_PKCS#1V15</li>
	 * </ul>
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm
	 */
	public final static String CRYPTOGRAHPER_KEY_ENCRYPTION_ALGORITHM = "CRYPTOGRAHPER_KEY_ENCRYPTION_ALGORITHM";

	/**
	 * String value that specifies the key encryption algorithm used to encrypt message keys by the SMIME cryptographer
	 * <br>Valid option values:
	 * <ul>
	 * <li>SHA1</li>
	 * <li>SHA256</li>
	 * <li>SHA384</li>
	 * <li>SHA512</li>
	 * </ul>
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptographer.smime.EncryptionAlgorithm
	 */
	public final static String CRYPTOGRAHPER_KEY_ENCRYPTION_DIGEST_ALGORITHM = "CRYPTOGRAHPER_KEY_ENCRYPTION_DIGEST_ALGORITHM";
	/**
	 * String value that specifies the digest algorithm used to hash messages by the SMIME cryptographer
	 * <br>Valid option values:
	 * <ul>
	 * <li>SHA1</li>
	 * <li>SHA256</li>
	 * <li>SHA384</li>
	 * <li>SHA512</li>
	 * </ul>
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptographer.smime.DigestAlgorithm
	 */
    public final static String CRYPTOGRAHPER_SMIME_DIGEST_ALGORITHM = "CRYPTOGRAHPER_SMIME_DIGEST_ALGORITHM";
    
    /**
     * Boolean value that determines if message digests should be logged.  If true, both the digest in the message signature and
     * the computed digest of message used for verification will be logged.  This is set to false by default. 
     * 
	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cryptographer.smime.LogDigests
     */
    public final static String CRYPTOGRAHPER_LOG_DIGESTS = "CRYPTOGRAHPER_LOG_DIGESTS";
 	/**
 	 * Boolean value that determines if the set of outgoing anchors can be used to trust incoming MDN and DSN messages.  This
 	 * is necessary to allow QoS to happen when messages are set to be outgoing only.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.cert.ldapresolver.UseOutgoingPolForNoficiations
 	 */
    public final static String USE_OUTGOING_POLICY_FOR_INCOMING_NOTIFICATIONS = "UseOutgoingPolicyForIncomingNotifications"; 
    
 	/**
 	 * Boolean value that determines if messages should be rejected if a routing header tamper has been detected.  A tamper condition
 	 * occurs when the SMTP RCPT TO envelope header or the MIME headers contain an address not found in the internal wrapped message headers.
 	 * <p><b>JVM Parameter/Options Name:</b> org.nhindirect.stagent.RejectOnRoutingTamper
 	 */
    public final static String REJECT_ON_ROUTING_TAMPER = "REJECT_ON_ROUTING_TAMPER"; 
    
	private final String paramName;
	private final String paramValue;
	
	/**
	 * Constructor
	 * @param name The name of the parameter
	 * @param value The String value of the parameter
	 */
	public OptionsParameter(final String name, final String value)
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Parameter name cannot be null or empty");
		
		this.paramName = name;
		this.paramValue = value;
	}
	
	/**
	 * Gets the parameter name.
	 * @return The parameter name
	 */
	public String getParamName()
	{
		return paramName;
	}
	
	/**
	 * Gets the parameter value.
	 * @return The parameter value
	 */
	public String getParamValue()
	{
		return paramValue;
	}
	
	/**
	 * Gets the value of an options parameter as an integer.
	 * @param param The parameter to retrieve the value from
	 * @param defaultValue The default value to return if the parameter is null, the parameter value is null, of if
	 * the value cannot be parse.
	 * @return The parameter value as an integer
	 */
	public static int getParamValueAsInteger(final OptionsParameter param , final int defaultValue)
	{
		int retVal = defaultValue;
		if (param != null && !StringUtils.isEmpty(param.getParamValue()))
		{
			try
			{
				retVal = Integer.parseInt(param.getParamValue());
			}
			catch (NumberFormatException e)
			{
				/*no-op, return default value */
			}
		}
		return retVal;
	}
	
	/**
	 * Gets the value of an options parameter as a boolean.
	 * @param param The parameter to retrieve the value from
	 * @param defaultValue The default value to return if the parameter is null, the parameter value is null, of if
	 * the value cannot be parse.
	 * @return The parameter value as a boolean
	 */
	public static boolean getParamValueAsBoolean(final OptionsParameter param , final boolean defaultValue)
	{
		boolean retVal = defaultValue;
		if (param != null && !StringUtils.isEmpty(param.getParamValue()))
			retVal = Boolean.parseBoolean(param.getParamValue());

		return retVal;
	}
}
