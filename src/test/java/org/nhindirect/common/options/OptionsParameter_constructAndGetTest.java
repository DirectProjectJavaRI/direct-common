package org.nhindirect.common.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OptionsParameter_constructAndGetTest
{	
	@Test
	public void testContructParamter() throws Exception
	{
		OptionsParameter param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, "Test Value");
		
		assertEquals(OptionsParameter.JCE_PROVIDER, param.getParamName());
		assertEquals("Test Value", param.getParamValue());
	}
	
	@Test
	public void testContructParamter_emptyValue() throws Exception
	{
		OptionsParameter param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, "");
		
		assertEquals(OptionsParameter.JCE_PROVIDER, param.getParamName());
		assertEquals("", param.getParamValue());
	}
	
	@Test
	public void testContructParamter_nullValue() throws Exception
	{
		OptionsParameter param = new OptionsParameter(OptionsParameter.JCE_PROVIDER, null);
		
		assertEquals(OptionsParameter.JCE_PROVIDER, param.getParamName());
		assertNull(param.getParamValue());
	}
	
	@Test
	public void testContructParamter_emptyName_assertException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new OptionsParameter("", "Test");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
	
	@Test
	public void testContructParamter_nullName_assertException() throws Exception
	{
		boolean exceptionOccured = false;
		
		try
		{
			new OptionsParameter(null, "Test");
		}
		catch (IllegalArgumentException e)
		{
			exceptionOccured = true;
		}

		assertTrue(exceptionOccured);
	}
}
