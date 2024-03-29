/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
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

package org.nhindirect.common.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;
import org.nhindirect.common.cert.SignerCertPair;
import org.nhindirect.common.cert.Thumbprint;
import org.nhindirect.common.options.OptionsManager;
import org.nhindirect.common.options.OptionsParameter;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility functions for searching for certificates.
 * @author Greg Meyer
 * @author Umesh Madan
 */
@SuppressWarnings("unchecked")
@Slf4j
public class CryptoExtensions 
{
	private static final String DEFAULT_JCE_PROVIDER_STRING = BouncyCastleProvider.PROVIDER_NAME;

	private static final String DEFAULT_SENSITIVE_JCE_PROVIDER_STRING = BouncyCastleProvider.PROVIDER_NAME;
	
	private static final String DEFAULT_JCE_PROVIDER_CLASS = "org.bouncycastle.jce.provider.BouncyCastleProvider";

	private static final String DEFAULT_SENSITIVE_JCE_PROVIDER_CLASS = "org.bouncycastle.jce.provider.BouncyCastleProvider";
	
	private static final int RFC822Name_TYPE = 1; // name type constant for Subject Alternative name email address
	private static final int DNSName_TYPE = 2; // name type constant for Subject Alternative name domain name	
	
	private static CertificateFactory certFactory;		
	
	static 
	{
		try
		{		
			certFactory = CertificateFactory.getInstance("X.509");
		}
		catch (CertificateException ex)
		{
			/*
			 * TODO: Handle Exception
			 */
		}
	}
	
	/**
	 * Typically JCE providers are registered through JVM properties files or statically calling {@link Security#addProvider(Provider)}.  The method 
	 * allows for configuration of JCE Providers through the {@link OptionsManager} classes.  This method iterates through a comma delimited set of providers,
	 * dynamically loads the provider class, and and registered each one if it has not already been registered.
	 * <p>
	 * If a provider is not configured via the {@link OptionsManager}, then the default BouncyCastle provider is registered (if it has not been
	 * already registered).
	 */
	public static void registerJCEProviders()
	{
		registerJCEProviders(null);
	}
	
	/**
	 * This variation of registerJCEProviders allows for a class loader to be specified for loading JCE Provider classes.  
	 * 
	 * @param clazzLoader  The class loader to use for dynamically loading Provider classes.  If this parameter is null, the CroptoExensions default
	 * class loader is used.
	 */
	public static void registerJCEProviders(ClassLoader clazzLoader)
	{
		// registering the default JCE providers
		String[] providerClasses = null;
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER_CLASSES);
		
		if (param == null || param.getParamValue() == null || param.getParamValue().isEmpty())
			providerClasses = new String[] {DEFAULT_JCE_PROVIDER_CLASS};
		else
			providerClasses = param.getParamValue().split(",");

		// register the provider classes
		for (String providerClass : providerClasses)
		{
			try
			{
				final Class<?> providerClazz = (clazzLoader == null) ?
						CryptoExtensions.class.getClassLoader().loadClass(providerClass) :
						clazzLoader.loadClass(providerClass);
				
				final Provider provider = Provider.class.cast(providerClazz.newInstance());
				
				// check to see if the provider is already registered
				if (Security.getProvider(provider.getName()) == null)
					Security.addProvider(provider);
				
			}
			catch (Exception e)
			{
				throw new IllegalStateException("Could not load and/or register JCE provider " + providerClass, e);
			}
		}
		
		// registering the default sensitive JCE providers
		providerClasses = null;
		param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_SENSITIVE_PROVIDER_CLASSES);
		
		if (param == null || param.getParamValue() == null || param.getParamValue().isEmpty())
			providerClasses = new String[] {DEFAULT_SENSITIVE_JCE_PROVIDER_CLASS};
		else
			providerClasses = param.getParamValue().split(",");

		// register the provider classes
		for (String providerClass : providerClasses)
		{
			try
			{
				
				Provider provider = null;
				Class<?> providerClazz = null;
				// check to see if the provider class string has parameters
				final String provParams[] = providerClass.split(";");
				if (provParams.length > 1)
				{
					providerClazz = CryptoExtensions.class.getClassLoader().loadClass(provParams[0]);
					try
					{
						Constructor<Provider> constr = Constructor.class.cast(providerClazz.getConstructor(String.class));
						provider = constr.newInstance(provParams[1]);
					}
					catch (InvocationTargetException e)
					{
						
						if (e.getTargetException() instanceof IllegalStateException)
						{
							log.warn("Could not create a JCE Provider with the specific parameter: {}",provParams[1], e);
						}
						else
							log.warn("JCE Provider param {} provided but not supported by JCE Provider implementation: {}", provParams[1], e.getMessage(), e);
					}
				}
				else
				{
					providerClazz = CryptoExtensions.class.getClassLoader().loadClass(providerClass);
				}
				
				if (provider == null)
				{
					provider = Provider.class.cast(providerClazz.newInstance());	
				}
				
				// check to see if the provider is already registered
				if (Security.getProvider(provider.getName()) == null)
					Security.addProvider(provider);
				
				/*
				Set<Service> services = provider.getServices();
				for (Service service : services)
				{
					System.out.println("Service: " + service.getAlgorithm() + "   Type:" + service.getType() + "\r\n\t" + service.toString());
				}
				System.out.println("\r\n\r\n\r\n");
				*/
			}
			catch (Exception e)
			{
				throw new IllegalStateException("Could not load and/or register sensitive JCE provider " + providerClass, e);
			}
		}		
	}
	
	/**
	 * Gets the configured JCE crypto provider string for crypto operations.  This is configured using the
	 * -Dorg.nhindirect.stagent.cryptography.JCEProviderName JVM parameters.  If the parameter is not set or is empty,
	 * then the default string "BC" (BouncyCastle provider) is returned.  By default the agent installs the BouncyCastle provider.
	 * @return The name of the JCE provider string.
	 */
	public static String getJCEProviderName()
	{
		String retVal = "";
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);
		
		if (param == null || param.getParamValue() == null || param.getParamValue().isEmpty())
			retVal = DEFAULT_JCE_PROVIDER_STRING;
		else
		{
			final String[] JCEString = param.getParamValue().split(",");
			retVal = JCEString[0];
		}
		return retVal;
	}
	
	/**
	 * Gets the configured JCE sensitive crypto provider string for crypto operations that need access to sensitive cryptogrophy information
	 * such as secret and private keys.  This is configured using the
	 * -Dorg.nhindirect.stagent.cryptography.JCESensitiveProviderName JVM parameters.  If the parameter is not set or is empty,
	 * then the default string "BC" (BouncyCastle provider) is returned.  By default the agent installs the BouncyCastle provider.
	 * @return The name of the JCE provider string.
	 */
	public static String getJCESensitiveProviderName()
	{
		String retVal = "";
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_SENTITIVE_PROVIDER);
		
		if (param == null || param.getParamValue() == null || param.getParamValue().isEmpty())
			retVal = DEFAULT_SENSITIVE_JCE_PROVIDER_STRING;
		else
		{
			final String[] JCEString = param.getParamValue().split(",");
			retVal = JCEString[0];
		}
		return retVal;
	}
	
	/**
	 * Gets the configured JCE crypto provider that supports the combination of the requested type and algorithm.  If a custom set of 
	 * providers has not been configured, this method will always return the default BouncyCatle provider string regardless if it matches
	 * the request type/algorithm pair.
	 * @param type The crypto type such as CertStore or CertPathValidator
	 * @param algorithm The algorithm such as PKIX or MAC.
	 * @return The name of the JCE provider string supporting the type/algorithm pair.
	 */
	public static String getJCEProviderNameForTypeAndAlgorithm(String type, String algorithm)
	{
		String[] JCEString = null;
		String retVal = "";
		final OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);
		
		if (param == null || param.getParamValue() == null || param.getParamValue().isEmpty())
			JCEString = new String[] {DEFAULT_JCE_PROVIDER_STRING};
		else
		{
			final String configuredJCEString = param.getParamValue();
			JCEString = configuredJCEString.split(",");
		}
		
		for(String provierString : JCEString)
		{
			final Provider provider = Security.getProvider(provierString);
			if (provider != null)
			{
				if (provider.getService(type, algorithm) != null)
				{
					retVal = provierString;
					break;
				}
			}
		}

		
		return retVal;
	}
	
	/**
	 * Overrides the configured JCE crypto provider string.  If the name is empty or null, the default string "BC" (BouncyCastle provider)
	 * is used.
	 * <P>
	 * The provider name may be a comma delimited list of provider strings.  The first string in the list will be the default provider string
     * and returned when using {@link #getJCEProviderName()}; however, the {@link #getJCEProviderNameForTypeAndAlgorithm(String, String)} will search
     * through the provider string until a valid provider that supports the requested type and algorithm is found.  In this case, the first matching
     * provider string will be used.
	 * @param name The name of the JCE provider.
	 */
	public static void setJCEProviderName(String name)
	{
		OptionsParameter param;
		
		if (name == null || name.isEmpty())
			param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, DEFAULT_JCE_PROVIDER_STRING);
		else
			param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, name);
		
		OptionsManager.getInstance().setOptionsParameter(param);
	}
	
	/**
	 * Compares the {@link Thumbprint thumbprints} of two certificates for equality.
	 * @param cert1 The first certificate to compare.
	 * @param cert2 The second certificate to compare.
	 * @return True if the certificates' thumbprints are equal.  False other wise.
	 */
	public static boolean isEqualThumbprint(X509Certificate cert1, X509Certificate cert2) throws CertificateException
	{
		
		return Thumbprint.toThumbprint(cert1).equals(Thumbprint.toThumbprint(cert2));
	}
	
    /**
     * Checks if the subject is contained in the certificates alternate subject names.  Specifically 
     * the rfc822Name name and DNSName types are checked.
     * @param cert The certificate to check.
     * @param subjectName The subject name to check in the alternate names.
     * @return True if the subjectName is contained in the alternate subject names.  False otherwise.
     * @deprecated As of 1.1.5.  Use {@link #certSubjectContainsName(X509Certificate, String)}
     */
    public static boolean containsEmailAddressInSubjectAltName(X509Certificate cert, String subjectName)
    {
        boolean searchingForEmailAddress = subjectName.toLowerCase(Locale.getDefault()).startsWith("emailaddress=");
        subjectName = searchingForEmailAddress ? subjectName.toLowerCase().replaceFirst("^emailaddress=", "") : subjectName;    	
    	
    	Collection<List<?>> altNames = null;
    	try
    	{
    		altNames = cert.getSubjectAlternativeNames();
    	}
    	catch (CertificateParsingException ex)
    	{
    		return false;
    	}	
		
    	if (altNames != null)
		{
    		for (List<?> entries : altNames)
    		{
    			if (entries.size() >= 2) // should always be the case according the altNames spec, but checking to be defensive
    			{
    				
    				Integer nameType = (Integer)entries.get(0);
    				if (nameType == RFC822Name_TYPE || nameType == DNSName_TYPE)
    				{
    					String name = (String)entries.get(1);
    					if (name.toLowerCase(Locale.getDefault()).equals(subjectName.toLowerCase()))
    						return true;
    				}
    				
    			}
    		}
		}
    	
    	return false;
    }	
	
	/**
	 * Checks if a name is contained in a certificate's alt subjects. 
	 * NOTE: The subject DN legacy email attribute is no longer supported.
	 * @param cert The certificate to check.
	 * @param name The name to search for in the certificate.
	 * @return True if the name is found in the certificate.  False otherwise.
	 */
    public static boolean certSubjectContainsName(X509Certificate cert, String name)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
    	
        if (cert == null)
        {
            throw new IllegalArgumentException("Certificate cannot be null.");
        }
        
    	boolean searchingForEmailAddress = name.toLowerCase(Locale.getDefault()).startsWith("emailaddress=");
        name = searchingForEmailAddress ? name.toLowerCase().replaceFirst("^emailaddress=", "") : name;    	

        String address = getSubjectAddress(cert);
        if (address == null || address.isEmpty())
        	return false;
    	                
        return name.toLowerCase(Locale.getDefault()).equals(address.toLowerCase(Locale.getDefault()));
    }	
	
    /**
     * Matches a common name in a certificate.
     * @param cert The certificate to check for the common name.
     * @param name The common name to check for.  This method automatically prefixes the name with "CN="
     * @return True if the common name is contained in the certificate.  False otherwise.
     * @deprecated As of 1.1.5.  Use {@link #certSubjectContainsName(X509Certificate, String)}
     */
    public static boolean matchName(X509Certificate cert, String name)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException();
        }

        String distinguishedName = "CN=" + name;
        return cert.getSubjectDN().getName().toUpperCase(Locale.getDefault()).contains(distinguishedName.toUpperCase(Locale.getDefault()));
    }

	/**
	 * Searches CMS signed data for a given email name.  Signed data may consist of multiple signatures either from the same subject of from multiple
	 * subjects. 
	 * @param signedData The signed data to search.
	 * @param name The name to search for in the list of signers.
	 * @param excludeNames A list of names to exclude from the list.  Because the search uses a simple "contains" search, it is possible for the name parameter
	 * to be a substring of what is requested.  The excludeNames contains a super string of the name to remove unwanted names from the returned list.  This parameter
	 * may be null;
	 * @return A colllection of pairs consisting of the singer's X509 certificated and signer information that matches the provided name.  Returns
	 * an empty collection if a signer matching the name cannot be found in the signed data.
	 */
    public static Collection<SignerCertPair> findSignersByName(CMSSignedData signedData, String name, Collection<String> excludeNames)
    {
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException();
        }

        Collection<SignerCertPair> retVal = null;
        
        try
        {
        	Store<X509CertificateHolder> certHolder = signedData.getCertificates();// getCertificatesAndCRLs("Collection", CryptoExtensions.getJCEProviderName());
	        SignerInformationStore  signers = signedData.getSignerInfos();
	        Collection<SignerInformation> c = signers.getSigners();
	        
	        for (SignerInformation signer : c)
	        {
	            Collection<X509CertificateHolder> certCollection = certHolder.getMatches(signer.getSID());
	            if (certCollection != null && certCollection.size() > 0)
	            {
	            
	            	X509CertificateHolder cert = certCollection.iterator().next();
	            	final X509Certificate theCert = (X509Certificate)certFactory.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
	            	if (certSubjectContainsName(theCert, name))
	            	{
	            		boolean exclude = false;
	            		
	            		// check if we need to exclude anything
	            		if (excludeNames != null)
	            			for (String excludeStr : excludeNames)
	            				if (certSubjectContainsName(theCert, excludeStr))
	            				{
	            					exclude = true;
	            					break;
	            				}
	            			
	            		if (exclude)
	            			continue; // break out and don't include this cert
	            		
	            		if (retVal == null)
	            			retVal = new ArrayList<SignerCertPair>();	            		
	            		
	            		retVal.add(new SignerCertPair(signer, convertToProfileProvidedCertImpl(theCert))); 
	            	}
	            } 
	        }
        }
        catch (Throwable e)
        {
        	
        }
        
        if (retVal == null)
        	return Collections.emptyList();
        
        return retVal;
    }

    /**
     * Searches a collection of X509Certificates for a certificate that matches the provided name.
     * @param certs The collection of certificates to search.
     * @param name The name to search for in the collection.
     * @return A certificate that matches the provided name.  Returns null if a matching certificate cannot be found in the collection.
     */
    public static X509Certificate findCertByName(Collection<X509Certificate> certs, String name)
    {
    	for (X509Certificate cert : certs)
    	{
    		if (certSubjectContainsName(cert, name))
    			return cert;
    	}
    	
    	return null;
    }
    
	/**
	 * Searches CMS signed data for a specific X509 certificate.
	 * @param signedData The signed data to search.
	 * @param name The certificate to search for in the signed data.
	 * @return A pair consisting of the singer's X509 certificated and signer information that matches the provided certificate.  Returns
	 * null if a signer matching the name cannot be found in the signed data.
	 */
    public static SignerCertPair findSignerByCert(CMSSignedData signedData, X509Certificate searchCert)
    {

    	if (searchCert == null)
        {
            throw new IllegalArgumentException();
        }

        try
        {	                	
        	SignerInformationStore  signers = signedData.getSignerInfos();
	        Collection<SignerInformation> c = signers.getSigners();
	        
	        for (SignerInformation signer : c)
	        {
	        	//signer.getSID().
	        	
	        	SignerId signerId = signer.getSID();

	        	if (signerId.getIssuer().equals(searchCert.getIssuerX500Principal()) && 
	        			signerId.getSerialNumber().equals(searchCert.getSerialNumber()))
	        	{
	        		return new SignerCertPair(signer, searchCert); 
	        	}	            			            	
	        }
        }
        catch (Exception e){}
        return null;
    }
    
	/*
	 * The certificate provider implementation may not be incomplete or may not provide all the necessary functionality such as 
	 * certificate verification.  This will convert the certificate into a cert backed by the default installed X509 certificate
	 * provider. 
	 */
    @SuppressWarnings("deprecation")
	private static X509Certificate convertToProfileProvidedCertImpl(X509Certificate certToConvert)
    {
    	X509Certificate retVal = null;
    	
    	try
    	{
    		InputStream stream = new BufferedInputStream(new ByteArrayInputStream(certToConvert.getEncoded()));
    	
    		retVal = (X509Certificate)certFactory.generateCertificate(stream);

    		IOUtils.closeQuietly(stream);	
    	}
    	catch (Exception e)
    	{
    		/*
    		 * TODO: handle exception
    		 */
    	}
    	
    	return retVal;
    }
    
    /**
     * Gets the address name associated with the certificate.  It may be an email address or a domain name.
     * @param certificate The certificate to search
     * @return The address of domain associated with a certificate.
     */
    public static String getSubjectAddress(X509Certificate certificate)
    {
    	String address = "";
    	// check alternative names first
    	Collection<List<?>> altNames = null;
    	try
    	{    		
    		altNames = certificate.getSubjectAlternativeNames();
    	}
    	catch (CertificateParsingException ex)
    	{
    		/* no -op */
    	}	
		
    	if (altNames != null)
		{
    		for (List<?> entries : altNames)
    		{
    			if (entries.size() >= 2) // should always be the case according the altNames spec, but checking to be defensive
    			{
    				
    				Integer nameType = (Integer)entries.get(0);
    				// prefer email over over domain?
    				if (nameType == RFC822Name_TYPE)    					
    					address = (String)entries.get(1);
    				else if (nameType == DNSName_TYPE && address.isEmpty())
    					address = (String)entries.get(1);    				
    			}
    		}
		}
    	

    	return address;
    	
    	/*
    	 * As of version ANSI/DS 2019-01-100-2021 approved on May 13, 20201, the legacy Email field of the
    	 * subject distinguished name is officially no longer supported for certificate binding to Direct addresses.
    	 * Only the subject alt name is now used.
    	 */
    }    
}
