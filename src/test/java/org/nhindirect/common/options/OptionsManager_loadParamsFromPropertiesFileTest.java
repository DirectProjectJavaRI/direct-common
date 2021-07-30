package org.nhindirect.common.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

public class OptionsManager_loadParamsFromPropertiesFileTest
{
	@BeforeEach
	public void setUp()
	{
		OptionsManager.destroyInstance();
	}
	
	@AfterEach
	public void tearDown()
	{
		OptionsManager.getInstance().options.clear();

	}
	
	@Test
	public void testloadParamsFromPropertiesFile_defaultPropertiesFile() throws Exception
	{
		System.setProperty(OptionsManager.OPTIONS_PROPERTIES_FILE_JVM_PARAM, "");
		File propFile = new File(OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		assertFalse(propFile.exists());
		
		propFile = new File(OptionsManager.DEFAULT_PROPERTIES_FILE);
		propFile.createNewFile();
		
		try (OutputStream outStream = FileUtils.openOutputStream(propFile, true);)
		{
			
			outStream.write("org.nhindirect.stagent.cryptography.JCEProviderName=SC".getBytes());
			outStream.flush();
		}
		finally
		{
		}
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);
		
		try
		{
			assertEquals("SC", param.getParamValue());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
			propFile.delete();
		}
	}
	
	@Test
	public void testloadParamsFromPropertiesFile_defaultPropertiesFile_fileDoesNotExist() throws Exception
	{
		File propFile = new File(OptionsManager.DEFAULT_PROPERTIES_FILE);
		assertFalse(propFile.exists());
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);

		assertNull(param);
	
	}
	
	@Test
	public void testloadParamsFromPropertiesFile_customPropertiesFile_fileDoesNotExist() throws Exception
	{
		File propFile = new File("./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);

		assertNull(param);
	
	}
	
	@Test
	public void testloadParamsFromPropertiesFile_customPropertiesFile() throws Exception
	{
		File propFile = new File("./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		System.setProperty("org.nhindirect.stagent.PropertiesFile", "./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		
		final String jvmPropValue = UUID.randomUUID().toString();
		
		try (OutputStream outStream = FileUtils.openOutputStream(propFile);)
		{
			
			
			final String value = "org.nhindirect.stagent.cryptography.JCEProviderName=" + jvmPropValue;
			outStream.write(value.getBytes());
			
		}
		finally
		{
		}
		
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);
		
		try
		{
			assertEquals(jvmPropValue, param.getParamValue());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.PropertiesFile", "");
			System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
			propFile.delete();
		}
	}
	
	@Test
	public void testloadParamsFromPropertiesFile_customPropertiesFile_paramIsNotAKnownJVMProp() throws Exception
	{
		File propFile = new File("./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		System.setProperty("org.nhindirect.stagent.PropertiesFile", "./target/props/" + OptionsManager.DEFAULT_PROPERTIES_FILE);
		
		final String jvmPropValue = UUID.randomUUID().toString();
		
		try (OutputStream outStream = FileUtils.openOutputStream(propFile);)
		{
			
			
			final String value = "testProperty=" + jvmPropValue;
			outStream.write(value.getBytes());
			
		}
		finally
		{
		}
		
		OptionsParameter param = OptionsManager.getInstance().getParameter("testProperty");
		
		try
		{
			assertEquals(jvmPropValue, param.getParamValue());
		}
		finally
		{
			System.setProperty("testProperty", "");
			System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
			propFile.delete();
		}
	}
	
	@Test
	public void testloadParamsFromPropertiesFile_defaultPropertiesFile_JVMOverridesProperty() throws Exception
	{
		File propFile = new File(OptionsManager.DEFAULT_PROPERTIES_FILE);
		if (propFile.exists())
			propFile.delete();
		
		
		try (OutputStream outStream = FileUtils.openOutputStream(propFile))
		{
			outStream.write("org.nhindirect.stagent.cryptography.JCEProviderName=SC".getBytes());
			
		}
		finally
		{
		}
		
		final String jvmPropValue = UUID.randomUUID().toString();
		System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", jvmPropValue);
		OptionsParameter param = OptionsManager.getInstance().getParameter(OptionsParameter.JCE_PROVIDER);
		
		try
		{
			assertEquals(jvmPropValue, param.getParamValue());
		}
		finally
		{
			System.setProperty("org.nhindirect.stagent.cryptography.JCEProviderName", "");
			propFile.delete();
		}
	}
	
}
