package org.nhindirect.common.options;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OptionsManager_initTest
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
	public void testInit_noParamsSet() throws Exception
	{
		OptionsManager manager = OptionsManager.getInstance();
		assertEquals(0, manager.options.size());
	}
	
	@Test
	public void testInit_noUnknownParam() throws Exception
	{
		OptionsManager manager = OptionsManager.getInstance();
		manager.initParam("Bogus Param Name");
		assertEquals(0, manager.options.size());
	}
	
	@Test
	public void testInit_emptyParamValue() throws Exception
	{
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
		
		OptionsManager manager = OptionsManager.getInstance();
		assertEquals(0, manager.options.size());

	}
	
	@Test
	public void testInit_populateParamValue() throws Exception
	{
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "BC");
		
		OptionsManager manager = OptionsManager.getInstance();
		assertEquals(1, manager.options.size());

		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
	}
}
