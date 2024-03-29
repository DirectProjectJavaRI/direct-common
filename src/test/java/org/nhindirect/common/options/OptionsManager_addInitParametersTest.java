package org.nhindirect.common.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OptionsManager_addInitParametersTest
{
	@BeforeEach
	public void setUp()
	{
		OptionsManager.INSTANCE = null;
	}
	
	@AfterEach
	public void tearDown()
	{
		OptionsManager.getInstance().options.clear();

	}
	
	@Test
	public void testAddInitParameters_optionsDoNotInitallyExist() 
	{
		// verify the options are not initially available
		final String jvmPropValue = UUID.randomUUID().toString();
		System.setProperty("doesntexist1", jvmPropValue);
		
		OptionsParameter param = OptionsManager.getInstance().getParameter("doesntexist1");
		assertNull(param);
		
		// now set the parameter
		Map<String, String> map = new HashMap<String, String>();
		map.put("doesntexist1", "doesntexist1");
		OptionsManager.addInitParameters(map);
		
		try
		{
			param = OptionsManager.getInstance().getParameter("doesntexist1");
			assertNotNull(param);
			assertEquals(jvmPropValue, param.getParamValue());
		}
		finally
		{
			System.setProperty("doesntexist1", "");
		}
	}
	
	@Test
	public void testAddInitParameters_noInstance_optionsDoNotInitallyExist() 
	{
		// verify the options are not initially available
		final String jvmPropValue = UUID.randomUUID().toString();
		System.setProperty("doesntexist1", jvmPropValue);
		
		
		// now set the parameter
		Map<String, String> map = new HashMap<String, String>();
		map.put("DOESNTEXIST1", "doesntexist1");
		
		assertNull(OptionsManager.INSTANCE);
		
		OptionsManager.addInitParameters(map);
		
		try
		{
			OptionsParameter param = OptionsManager.getInstance().getParameter("DOESNTEXIST1");
			assertNotNull(param);
			assertEquals(jvmPropValue, param.getParamValue());
		}
		finally
		{
			System.setProperty("doesntexist1", "");
		}
	}
	
	@Test
	public void testAddInitParameters_optionsInitallyExist() 
	{

		final String jvmPropValue = UUID.randomUUID().toString();
		System.setProperty("org.nhindirect.stagent.cert.ldapresolver.MaxCacheSize", jvmPropValue);
		
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.LDAP_CERT_RESOLVER_MAX_CACHE_SIZE);
		assertNotNull(param);
		
		// now set the parameter
		final String jvmPropValue2 = UUID.randomUUID().toString();
		System.setProperty("org.nhindirect.stagent.cert.ldapresolver.MaxCacheSize", jvmPropValue2);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(OptionsParameter.LDAP_CERT_RESOLVER_MAX_CACHE_SIZE, "org.nhindirect.stagent.cert.ldapresolver.MaxCacheSize");
		
		
		OptionsManager.addInitParameters(map);
		
		try
		{
			param = OptionsManager.getInstance().getParameter(OptionsParameter.LDAP_CERT_RESOLVER_MAX_CACHE_SIZE);
			assertNotNull(param);
			assertEquals(jvmPropValue2, param.getParamValue());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cert.ldapresolver.MaxCacheSize", "");
		}
	}
	
}
