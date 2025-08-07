package org.nhindirect.common.util;

import java.security.Provider;
import java.security.Security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

public class TestUtils 
{	
    /**
     * used for testing with a pkcs11 token
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
			p.configure(configName);
			Security.addProvider(p);

		}
		catch (Exception e)
		{
			return "";
		}

		return p.getName();
	}
}
