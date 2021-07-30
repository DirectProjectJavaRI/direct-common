package org.nhindirect.common.cfenv.r2dbc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ResourceUtils;

import io.pivotal.cfenv.test.AbstractCfEnvTests;
import mockit.integration.junit5.JMockitExtension;

@ExtendWith(JMockitExtension.class)
public class CfR2DBCDataSourceEnvironmentPostProcessorTest extends AbstractCfEnvTests
{
	private final CfR2DBCDataSourceEnvironmentPostProcessor environmentPostProcessor = new CfR2DBCDataSourceEnvironmentPostProcessor();

	private final ConfigurableApplicationContext context = new AnnotationConfigApplicationContext();
	
	@Test
	public void testParseVCAPToR2dbcProps() throws Exception
	{
		System.setProperty("VCAP_APPLICATION", "yes");
		
		try
		{
			// To setup values used by CfEnv
			File file = ResourceUtils.getFile("classpath:cf/vcap-services.json");
			String fileContents = new String(Files.readAllBytes(file.toPath()));
			mockVcapServices(fileContents);
			
			environmentPostProcessor.postProcessEnvironment(this.context.getEnvironment(), null);
			
			final String url = this.context.getEnvironment().getProperty("spring.r2dbc.url");

			assertEquals("r2dbcs:mysql://10.0.4.35:3306/cf_2e23d10a_8738_8c3c_66cf_13e44422698c?useSSL=true&requireSSL=true", url);
		}
		finally
		{
			System.clearProperty("VCAP_APPLICATION");
		}
	}
}
