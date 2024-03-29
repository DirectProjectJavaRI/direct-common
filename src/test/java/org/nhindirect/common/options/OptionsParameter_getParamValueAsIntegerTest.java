package org.nhindirect.common.options;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;


public class OptionsParameter_getParamValueAsIntegerTest
{
	@Test
	public void testGetParamValueAsInteger_nullParam_returnDefaultVal()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(null, defaultVal);
		
		assertEquals(defaultVal, retVal);
	}
	
	@Test
	public void testGetParamValueAsInteger_nullParamValue_returnDefaultVal()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(new OptionsParameter("test", null), defaultVal);
		
		assertEquals(defaultVal, retVal);
	}
	
	@Test
	public void testGetParamValueAsInteger_emptyParamValue_returnDefaultVal()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(new OptionsParameter("test", ""), defaultVal);
		
		assertEquals(defaultVal, retVal);
	}
	
	@Test
	public void testGetParamValueAsInteger_invalidIntFormat_returnDefaultVal()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(new OptionsParameter("test", "i-/ekux"), defaultVal);
		
		assertEquals(defaultVal, retVal);
	}
	
	@Test
	public void testGetParamValueAsInteger_validValue_returnValue()
	{
		SecureRandom random = new SecureRandom();
		random.setSeed(System.currentTimeMillis());
		
		int defaultVal = random.nextInt();
		int retVal = OptionsParameter.getParamValueAsInteger(new OptionsParameter("test", "5"), defaultVal);
		
		assertEquals(5, retVal);
	}
}
