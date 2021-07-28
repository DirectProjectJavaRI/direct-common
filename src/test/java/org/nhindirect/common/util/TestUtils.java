package org.nhindirect.common.util;

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
	@SuppressWarnings("restriction")
	public static String setupSafeNetToken() throws Exception
	{	
		final CallbackHandler handler = new CallbackHandler()
		{
			public void	handle(Callback[] callbacks)
			{
				for (Callback callback : callbacks)
				{
					if (callback instanceof PasswordCallback)
					{		
						
						 ((PasswordCallback)callback).setPassword("1Kingpuff".toCharArray());
					
					}
				}
			}
		};
		
		sun.security.pkcs11.SunPKCS11 p = null;
		try
		{
			final String configName = "./src/test/resources/pkcs11Config/pkcs11.cfg";
			p = new sun.security.pkcs11.SunPKCS11(configName);
			Security.addProvider(p);
			p.login(null, handler);

		}
		catch (Exception e)
		{
			return "";
		}

		return p.getName();
	}
}
