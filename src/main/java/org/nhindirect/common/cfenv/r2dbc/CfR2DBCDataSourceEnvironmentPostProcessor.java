package org.nhindirect.common.cfenv.r2dbc;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import com.google.common.collect.Maps;

import io.pivotal.cfenv.jdbc.CfJdbcEnv;
import io.pivotal.cfenv.jdbc.CfJdbcService;
import io.pivotal.cfenv.spring.boot.CfDataSourceEnvironmentPostProcessor;
import io.pivotal.cfenv.spring.boot.DeferredLog;

public class CfR2DBCDataSourceEnvironmentPostProcessor extends CfDataSourceEnvironmentPostProcessor
{
	/**
	 * Disallowed query options by R2DBC SPI
	 */
    private static final Set<String> PROHIBITED_QUERY_OPTIONS =  Stream.of("database", "driver", 
            "host", "password", "port", "protocol", "user")
            .collect(Collectors.toSet());
	
	private static DeferredLog DEFERRED_LOG = new DeferredLog();
	
	private static int invocationCount;
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) 
	{
		increaseInvocationCount();
		if (CloudPlatform.CLOUD_FOUNDRY.isActive(environment)) 
		{
			final CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
			CfJdbcService cfJdbcService;
			try 
			{
				cfJdbcService = cfJdbcEnv.findJdbcService();
			}
			catch (Exception e) 
			{
				if (invocationCount == 1) 
				{
					DEFERRED_LOG.debug("Skipping execution of CfR2DBCDataSourceEnvironmentPostProcessor. " + e.getMessage());
				}
				return;
			}
			if (cfJdbcService != null) 
			{
				
				Map<String, Object> properties = new LinkedHashMap<>();
				final Map.Entry<String, Map<String, String>> processedUrl = processUrl(cfJdbcService.getUrl());
				
				String url = processedUrl.getKey();
				
				if (url.startsWith("jdbc:"))
				{
					// check for TLS when replacing the jdbc string
					boolean useTLS = false;
					for (Map.Entry<String, String> entry : processedUrl.getValue().entrySet())
					{
						if (entry.getKey().compareToIgnoreCase("useSSL") == 0  && Boolean.parseBoolean(entry.getValue()))
						{
							useTLS = true;
							break;
						}
					}
					url = url.replace("jdbc:", (useTLS) ? "r2dbcs:" : "r2dbc:");
				}
				
				properties.put("spring.r2dbc.url", url);
				properties.put("spring.r2dbc.username", cfJdbcService.getUsername());
				properties.put("spring.r2dbc.password", cfJdbcService.getPassword());


				MutablePropertySources propertySources = environment.getPropertySources();
				if (propertySources.contains(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME)) 
				{
					propertySources.addAfter(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME,
							new MapPropertySource("cfenvr2dbc", properties));
				}
				else 
				{
					propertySources.addFirst(new MapPropertySource("cfenvr2dbc", properties));
				}
				if (invocationCount == 1) 
				{
					DEFERRED_LOG.info("Setting spring.datasource properties from bound service ["
									+ cfJdbcService.getName() + "]");
				}
			}
		}
		else 
		{
			DEFERRED_LOG.debug("Not setting spring.datasource.url, not in Cloud Foundry Environment");
		}
	}

	protected Map.Entry<String, Map<String, String>> processUrl(String url)
	{
		final Map<String, String> retQueryMap = new HashMap<>();
		
		String retURL = "";
	
        final String[] schemeParts = url.split(":", 3);

        final String scheme = schemeParts[0];

        int schemeSpecificPartIndex = url.indexOf("://");
        final String rewrittenUrl = scheme + url.substring(schemeSpecificPartIndex);

        final URI uri = URI.create(rewrittenUrl);
        
        if (!StringUtils.isEmpty(uri.getRawQuery())) 
        {
        	int idx = url.indexOf("?");
        	final StringBuilder urlBuilder = new StringBuilder(url.substring(0, idx));
        	final MutableBoolean startQuery = new MutableBoolean(true);
        	
            parseQuery(uri.getRawQuery().trim(), (k, v) -> 
            {

                if (!PROHIBITED_QUERY_OPTIONS.contains(k)) 
                {
                	if (startQuery.isTrue()) 
                	{
                		urlBuilder.append("?");
                		startQuery.setFalse();
                	}
                	else
                		urlBuilder.append("&");
                                	
                	urlBuilder.append(k).append("=").append(v);
                }

                retQueryMap.put(k, v);
            });
            
            retURL = urlBuilder.toString();
        }
        else
        	retURL = url;

        return Maps.immutableEntry(retURL, retQueryMap);
        
	}
	
	private void increaseInvocationCount() 
	{
		synchronized (this) 
		{
			invocationCount++;
		}
	}
	
    static void parseQuery(CharSequence s, BiConsumer<String, String> tupleConsumer) 
    {
        QueryStringParser parser = QueryStringParser.create(s);

        while (!parser.isFinished()) 
        {

            CharSequence name = parser.parseName();
            CharSequence value = parser.isFinished() ? null : parser.parseValue();

            if (name.length() != 0 && value != null) 
            {
                tupleConsumer.accept(decode(name).toString(), decode(value).toString());
            }
        }
    }
    
    static class QueryStringParser 
    {
    	
        /**
         * carriage return (ASCII 13).
         */
        static final char CR = '\r';

        /**
         * line feed (ASCII 10).
         */
        static final char LF = '\n';

        /**
         * space (ASCII 32).
         */
        static final char SPACE = ' ';

        /**
         * horizontal-tab (ASCII 9).
         */
        static final char TAB = '\t';

        private final CharSequence input;

        private final Cursor state;

        private final BitSet delimiters = new BitSet(256);

        private QueryStringParser(CharSequence input) 
        {
            this.input = input;
            this.state = new Cursor(input.length());
            this.delimiters.set('&'); // ampersand, tuple separator
        }

        
        static QueryStringParser create(CharSequence input) 
        {
            return new QueryStringParser(input);
        }


        boolean isFinished() 
        {
            return state.isFinished();
        }


        CharSequence parseName() 
        {

            if (this.state.isFinished()) {
                throw new IllegalStateException("Parsing is finished");
            }

            delimiters.set('=');
            return parseToken();
        }


        CharSequence parseValue() 
        {
            if (this.state.isFinished()) 
            {
                throw new IllegalStateException("Parsing is finished");
            }

            int delim = this.input.charAt(this.state.getParsePosition());
            this.state.incrementParsePosition();

            if (delim == '=') 
            {
                delimiters.clear('=');
                try 
                {
                    return parseToken();
                } 
                finally 
                {
                    if (!isFinished()) 
                    {
                        this.state.incrementParsePosition();
                    }
                }
            }

            return null;
        }

        private CharSequence parseToken() 
        {

            StringBuilder dst = new StringBuilder();

            boolean whitespace = false;

            while (!this.state.isFinished()) 
            {
                char current = this.input.charAt(this.state.getParsePosition());
                if (delimiters.get(current)) 
                {
                    break;
                } 
                else if (isWhitespace(current)) 
                {
                    skipWhiteSpace();
                    whitespace = true;
                } 
                else 
                {
                    if (whitespace && dst.length() > 0) 
                    {
                        dst.append(' ');
                    }
                    copyContent(dst);
                    whitespace = false;
                }
            }

            return dst;
        }


        private void skipWhiteSpace() 
        {
            int pos = this.state.getParsePosition();

            for (int i = this.state.getParsePosition(); i < this.state.getUpperBound(); i++) 
            {
                char current = this.input.charAt(i);
                if (!isWhitespace(current)) 
                {
                    break;
                }
                pos++;
            }

            this.state.updatePos(pos);
        }

        private void copyContent(StringBuilder target) 
        {
            int pos = this.state.getParsePosition();

            for (int i = this.state.getParsePosition(); i < this.state.getUpperBound(); i++) 
            {
                char current = this.input.charAt(i);
                if (delimiters.get(current) || isWhitespace(current)) 
                {
                    break;
                }
                pos++;
                target.append(current);
            }

            this.state.updatePos(pos);
        }

        private static boolean isWhitespace(char ch) 
        {
            return ch == SPACE || ch == TAB || ch == CR || ch == LF;
        }
    }

    private static class Cursor 
    {

        private final int upperBound;

        private int pos;

        Cursor(int upperBound) 
        {
            this.upperBound = upperBound;
            this.pos = 0;
        }

        void incrementParsePosition() 
        {
            updatePos(getParsePosition() + 1);
        }

        int getUpperBound() 
        {
            return this.upperBound;
        }

        int getParsePosition() 
        {
            return this.pos;
        }

        void updatePos(final int pos) 
        {
            this.pos = pos;
        }

        boolean isFinished() 
        {
            return this.pos >= this.upperBound;
        }

    }
	
    
    private static CharSequence decode(CharSequence s) 
    {

        boolean encoded = false;
        int numChars = s.length();
        StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
        int i = 0;

        char c;
        byte[] bytes = null;

        while (i < numChars) 
        {
            c = s.charAt(i);
            switch (c) 
            {
                case '+':
                    sb.append(' ');
                    i++;
                    encoded = true;
                    break;
                case '%':
                    /*
                     * Starting with this instance of %, process all
                     * consecutive substrings of the form %xy. Each
                     * substring %xy will yield a byte. Convert all
                     * consecutive  bytes obtained this way to whatever
                     * character(s) they represent in the provided
                     * encoding.
                     */
                    try 
                    {

                        // (numChars-i)/3 is an upper bound for the number
                        // of remaining bytes
                        if (bytes == null) 
                        {
                            bytes = new byte[(numChars - i) / 3];
                        }
                        int pos = 0;

                        while (((i + 2) < numChars) && (c == '%')) 
                        {
                            int v = Integer.parseInt(s.subSequence(i + 1, i + 3).toString(), 16);
                            if (v < 0) 
                            {
                                throw new IllegalArgumentException(
                                    "URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
                            }
                            bytes[pos++] = (byte) v;
                            i += 3;
                            if (i < numChars) {
                                c = s.charAt(i);
                            }
                        }

                        // A trailing, incomplete byte encoding such as
                        // "%x" will cause an exception to be thrown

                        if ((i < numChars) && (c == '%')) 
                        {
                            throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                        }

                        sb.append(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes, 0, pos)));
                    } 
                    catch (NumberFormatException e) 
                    {
                        throw new IllegalArgumentException(
                            "URLDecoder: Illegal hex characters in escape (%) pattern - " + e.getMessage());
                    }
                    encoded = true;
                    break;
                default:
                    sb.append(c);
                    i++;
                    break;
            }
        }

        return (encoded ? sb : s);
    }    
    
    
}
