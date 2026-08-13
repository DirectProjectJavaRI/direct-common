package org.nhindirect.common.mail.dsn;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.mail.Address;
import jakarta.mail.Header;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.junit.jupiter.api.Test;
import org.nhindirect.common.mail.MailStandard;
import org.nhindirect.common.mail.dsn.DSNStandard.DSNAction;
import org.nhindirect.common.mail.dsn.DSNStandard.DSNStatus;
import org.nhindirect.common.mail.dsn.DSNStandard.MtaNameType;
import org.nhindirect.common.mail.dsn.impl.DefaultDSNFailureTextBodyPartGenerator;
import org.nhindirect.common.mail.dsn.impl.HumanReadableTextAssemblerFactory;



public class DNSGenerator_CreateDSNMessageTest 
{
	@Test
	public void testCreateDSNMessage_createGeneralDSNMessage() throws Exception
	{
		final DSNGenerator dsnGenerator = new DSNGenerator("Not Delivered:");
		
    	final DSNRecipientHeaders dsnRecipHeaders = 
    			new DSNRecipientHeaders(DSNAction.FAILED, 
    			DSNStatus.getStatus(DSNStatus.PERMANENT, DSNStatus.UNDEFINED_STATUS), new InternetAddress("ah4626@test.com"));
		
    	final List<DSNRecipientHeaders> dsnHeaders = new ArrayList<DSNRecipientHeaders>();
    	dsnHeaders.add(dsnRecipHeaders);
    	
    	final String originalMessageId = UUID.randomUUID().toString();
    	final DSNMessageHeaders messageDSNHeaders = new DSNMessageHeaders("DirectJUNIT", originalMessageId, MtaNameType.DNS);
    	
    	List<Address> faileRecips = new ArrayList<Address>();
    	faileRecips.add(new InternetAddress("ah4626@test.com"));
    	
    	final DefaultDSNFailureTextBodyPartGenerator textGenerator = new DefaultDSNFailureTextBodyPartGenerator("", "", "",
    		    "", "", HumanReadableTextAssemblerFactory.getInstance());
    	
    	
    	
    	final MimeBodyPart textBodyPart = textGenerator.generate(new InternetAddress("gm2552@test.com"), faileRecips, null);
    	
		MimeMessage dsnMessage = dsnGenerator.createDSNMessage(new InternetAddress("gm2552@test.com"), "test", new InternetAddress("postmaster@test.com"), 
				dsnHeaders, messageDSNHeaders, textBodyPart);
		
		assertNotNull(dsnMessage);
		assertEquals("postmaster@test.com", MailStandard.getHeader(dsnMessage, MailStandard.Headers.From));
		assertEquals("gm2552@test.com", MailStandard.getHeader(dsnMessage, MailStandard.Headers.To));
		assertTrue( MailStandard.getHeader(dsnMessage, MailStandard.Headers.Subject).startsWith("Not Delivered:"));
		assertTrue(!MailStandard.getHeader(dsnMessage, MailStandard.Headers.Date).isEmpty());
		
	}

	@Test
	public void testCreateDSNMessage_createDSNMessageWithOriginalHeaders() throws Exception
	{
		final DSNGenerator dsnGenerator = new DSNGenerator("Not Delivered:");
		
		final DSNRecipientHeaders dsnRecipHeaders = 
				new DSNRecipientHeaders(DSNAction.FAILED, 
						DSNStatus.getStatus(DSNStatus.PERMANENT, DSNStatus.UNDEFINED_STATUS), new InternetAddress("ah4626@test.com"));
		
		final List<DSNRecipientHeaders> dsnHeaders = new ArrayList<DSNRecipientHeaders>();
		dsnHeaders.add(dsnRecipHeaders);
		
		final String originalMessageId = UUID.randomUUID().toString();
		final DSNMessageHeaders messageDSNHeaders = new DSNMessageHeaders("DirectJUNIT", originalMessageId, MtaNameType.DNS);
		
		List<Address> faileRecips = new ArrayList<Address>();
		faileRecips.add(new InternetAddress("ah4626@test.com"));
		
		List<Header> originalMessageHeaders = new ArrayList<Header>();
		originalMessageHeaders.add(new Header("Date", "Tue, 11 Jun 2015 02:43:38 -0500 (CDT)"));
		originalMessageHeaders.add(new Header("From", "\"Smith, John\" <from@test.com>"));
		originalMessageHeaders.add(new Header("To", "\"Ben & Jerry\" <benandjerry@test.com>"));
		originalMessageHeaders.add(new Header("Subject", "subject \0goes here")); // this subject contains a NUL character, which should get removed
		
		final DefaultDSNFailureTextBodyPartGenerator textGenerator = new DefaultDSNFailureTextBodyPartGenerator("", "%headers_tag%", "",
				"", "", HumanReadableTextAssemblerFactory.getInstance());
		
		
		
		final MimeBodyPart textBodyPart = textGenerator.generate(new InternetAddress("gm2552@test.com"), faileRecips, Collections.enumeration(originalMessageHeaders));
		
		MimeMessage dsnMessage = dsnGenerator.createDSNMessage(new InternetAddress("gm2552@test.com"), "test", new InternetAddress("postmaster@test.com"), 
				dsnHeaders, messageDSNHeaders, textBodyPart);
		
		assertNotNull(dsnMessage);
		MimeBodyPart htmlBodyPart = (MimeBodyPart) ((MimeMultipart) dsnMessage.getContent()).getBodyPart(0);
		String htmlContent = (String) htmlBodyPart.getContent();
		assertThat(htmlContent, containsString("subject goes here")); // NUL is removed
		assertThat(htmlContent, containsString("Ben &amp; Jerry")); // ampersand is encoded
		assertThat(htmlContent, containsString("&lt;from@test.com&gt;")); // <> are encoded
	}
}
