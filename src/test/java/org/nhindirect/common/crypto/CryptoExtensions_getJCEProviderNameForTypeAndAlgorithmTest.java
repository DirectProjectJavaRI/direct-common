package org.nhindirect.common.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nhindirect.common.options.OptionsManager;
import org.nhindirect.common.options.OptionsManagerUtils;
import org.nhindirect.common.options.OptionsParameter;


public class CryptoExtensions_getJCEProviderNameForTypeAndAlgorithmTest
{
	@BeforeEach
	public void setUp()
	{
		OptionsManagerUtils.clearOptionsManagerInstance();
		
	}
	
	@AfterEach
	public void tearDown()
	{
		OptionsManagerUtils.clearOptionsManagerOptions();
		OptionsManagerUtils.clearOptionsManagerInstance();
	}
	
	@Test
	public void testGetJCEProviderNameForTypeAndAlgorithm_noConfiguredJCENames_assertEmptyBCProvider()
	{
		CryptoExtensions.registerJCEProviders();
		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}
	
	@Test
	public void testGetJCEProviderNameForTypeAndAlgorithm_nullConfiguredJCENames_assertEmptyBCProvider()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, null));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}
	
	@Test
	public void testGetJCEProviderNameForTypeAndAlgorithm_emptyConfiguredJCENames_assertEmptyBCProvider()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, ""));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}	
	
	@Test
	public void testGetJCEProviderNameForTypeAndAlgorithm_configuredJCENames_algAndTypeNotFound_assertEmptyProviderName()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, "BC"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("BC", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}
	
	@Test
	public void testGetJCEProviderNameForTypeAndAlgorithm_configuredJCENames_unknownProvider_assertEmptyProviderName()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, "dummy"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("dummy", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("doesnt matter", "doesnt matter"));
	}	
	
	@Test
	public void testGetJCEProviderNameForTypeAndAlgorithm_configuredJCENames_foundProvider_assertProviderName()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, "BC"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("BC", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("BC", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("CertPathValidator", "PKIX"));
	}	
	
	@Test
	public void testGetJCEProviderNameForTypeAndAlgorithm_multipConfiguredJCENames_foundProvider_assertProviderName()
	{
		CryptoExtensions.registerJCEProviders();
		
		OptionsManager.getInstance().setOptionsParameter(new OptionsParameter(OptionsParameter.JCE_PROVIDER, "MOCK,BC"));
		assertNotNull(OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER));
		assertEquals("MOCK,BC", OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER).getParamValue());

		
		assertEquals("BC", CryptoExtensions.getJCEProviderNameForTypeAndAlgorithm("CertPathValidator", "PKIX"));
	}			
}
