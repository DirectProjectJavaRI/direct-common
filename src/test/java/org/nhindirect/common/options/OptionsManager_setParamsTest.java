package org.nhindirect.common.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class OptionsManager_setParamsTest
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
		
		OptionsParameter param1 = new OptionsParameter("Test Param1", "Test Value");
		OptionsParameter param2 = new OptionsParameter("Test Param2", "Test Value");
		
		mgr.setOptionsParameters(Arrays.asList(param1, param2));
		
		assertEquals(2, mgr.getParameters().size());
		
		
		OptionsParameter retParam = mgr.getParameter("Test Param1");
		assertNotNull(retParam);
		assertEquals(param1.getParamName(), retParam.getParamName());
		assertEquals(param1.getParamValue(), retParam.getParamValue());
	}
	
	@Test
	public void testSetParams_overrideExistantParam() throws Exception
	{		
		OptionsManager mgr = OptionsManager.getInstance();
		assertNull(mgr.getParameter("Test Param"));
		
		OptionsParameter param = new OptionsParameter("Test Param", "Test Value");
		
		mgr.setOptionsParameters(Arrays.asList(param));
		assertEquals(1, mgr.getParameters().size());
		
		
		OptionsParameter retParam = mgr.getParameter("Test Param");
		assertNotNull(retParam);
		assertEquals(param.getParamName(), retParam.getParamName());
		assertEquals(param.getParamValue(), retParam.getParamValue());
		
		param = new OptionsParameter("Test Param", "Test Value2");
		
		mgr.setOptionsParameters(Arrays.asList(param));
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
		
		mgr.setOptionsParameters(Arrays.asList(param));
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
			mgr.setOptionsParameters(null);
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
}
