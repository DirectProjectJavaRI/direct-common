package org.nhindirect.common.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OptionsParameter_getParamValueAsBooleanTest
{
	@Test
	public void testGetParamValueAsBoolean_nullParam_returnDefaultVal()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(null, true);
		
		assertTrue(retVal);
	}
	
	@Test
	public void testGetParamValueAsBoolean_nullParamValue_returnDefaultVal()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", null), true);
		
		assertTrue(retVal);
	}
	
	@Test
	public void testGetParamValueAsBoolean_emptyParamValue_returnDefaultVal()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", ""), true);
		
		assertEquals(true, retVal);
	}
	
	@Test
	public void testGetParamValueAsBoolean_invalidBooleanFormat_returnFalse()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", "i-/ekux"), true);
		
		assertFalse(retVal);
	}
	
	@Test
	public void testGetParamValueAsBoolean_validTrueValue_returnValue()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", "true"), true);
		
		assertTrue(retVal);
	}
	
	@Test
	public void testGetParamValueAsBoolean_validFalseValue_returnValue()
	{
		boolean retVal = OptionsParameter.getParamValueAsBoolean(new OptionsParameter("test", "false"), true);
		
		assertFalse(retVal);
	}
}
