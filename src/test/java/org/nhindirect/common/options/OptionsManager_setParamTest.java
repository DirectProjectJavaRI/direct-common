package org.nhindirect.common.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OptionsManager_setParamTest
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
	public void testSetParams_emptyParams_setNonExistantParam() throws Exception
	{
		OptionsManager mgr = OptionsManager.getInstance();
		assertNull(mgr.getParameter("Test Param"));
		assertEquals(0, mgr.getParameters().size());
		
		OptionsParameter param = new OptionsParameter("Test Param", "Test Value");
		
		mgr.setOptionsParameter(param);
		
		
		assertEquals(1, mgr.getParameters().size());
		
		
		OptionsParameter retParam = mgr.getParameter("Test Param");
		assertNotNull(retParam);
		assertEquals(param.getParamName(), retParam.getParamName());
		assertEquals(param.getParamValue(), retParam.getParamValue());
	}
	
	@Test
	public void testSetParams_overrideExistantParam() throws Exception
	{		
		OptionsManager mgr = OptionsManager.getInstance();
		assertNull(mgr.getParameter("Test Param"));
		
		OptionsParameter param = new OptionsParameter("Test Param", "Test Value");
		
		mgr.setOptionsParameter(param);
		assertEquals(1, mgr.getParameters().size());
		
		
		OptionsParameter retParam = mgr.getParameter("Test Param");
		assertNotNull(retParam);
		assertEquals(param.getParamName(), retParam.getParamName());
		assertEquals(param.getParamValue(), retParam.getParamValue());
		
		param = new OptionsParameter("Test Param", "Test Value2");
		
		mgr.setOptionsParameter(param);
		assertEquals(1, mgr.getParameters().size());
		
		retParam = mgr.getParameter("Test Param");
		assertNotNull(retParam);
		assertEquals(param.getParamName(), retParam.getParamName());
		assertEquals(param.getParamValue(), retParam.getParamValue());
	}
	
	@Test
	public void testSetParams_overrideJVMParam() throws Exception
	{		
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "BC");
		OptionsManager mgr = OptionsManager.getInstance();
		assertNotNull(OptionsParameter.JCE_PROVIDER);
		
		OptionsParameter param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, "NSS");
		
		mgr.setOptionsParameter(param);
		assertEquals(1, mgr.getParameters().size());
		
		
		OptionsParameter retParam = mgr.getParameter(OptionsParameter.JCE_PROVIDER);
		assertNotNull(retParam);
		assertEquals(param.getParamName(), retParam.getParamName());
		assertEquals(param.getParamValue(), retParam.getParamValue());
		
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
	}
	
	@Test
	public void testSetParams_nullParameter_assertException() throws Exception
	{		
		OptionsManager mgr = OptionsManager.getInstance();
		
		boolean exceptionOccured = false;
		try
		{
			mgr.setOptionsParameter(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
}
