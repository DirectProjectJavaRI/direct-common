package org.nhindirect.common.util;

import java.security.Provider;
import java.security.Security;
import java.security.Provider.Service;
import java.util.Set;

public class TestUtils 
{	
    /**
     * used for testing with a pkcs11 token
     * to login to the token, use an appropriate KeyStoreProtectionManager instance which will instigate the login with a KeyStore.load() call
     * @return The Security provider name if the token is loaded successfully... an empty string other wise 
     * @throws Exception
     */
	public static String setupSafeNetToken() throws Exception
	{	
		
		Provider p = null;
		try
		{
			final String configName = "./src/test/resources/pkcs11Config/pkcs11.cfg";
			p = Security.getProvider("SunPKCS11");
			p = p.configure(configName);
			Security.addProvider(p);
			Set<Service> services = p.getServices();
			if (services.size() == 0)
				return "";
		}
		catch (Exception e)
		{
			return "";
		}

		return p.getName();
	}
}
