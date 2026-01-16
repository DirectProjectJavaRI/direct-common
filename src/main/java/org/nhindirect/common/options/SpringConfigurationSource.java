package org.nhindirect.common.options;

import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * A ConfigurationSource whose underlying source is the Spring configuration context.
 */
public class SpringConfigurationSource implements ConfigurationSource {

	private final Environment env;
	
	/**
	 * Creates the ConfigurationSource from a Spring application context.
	 * @param ctx The Spring application context to use for configuration retrieval.
	 * @return The ConfigurationSource whose underlying source will be the application context.
	 */
	public static SpringConfigurationSource of(ApplicationContext ctx) {
		return new SpringConfigurationSource(ctx.getEnvironment());
	}
	
	/**
	 * Creates the configuration source from a Spring Environment.
	 * @param ctx The Spring Environment to use for configuration retrieval.
	 * @return The ConfigurationSource whose underlying source will be the Spring Environment.
	 */
	public static SpringConfigurationSource of(Environment env) {
		return new SpringConfigurationSource(env);
	}

	/*
	 * private constructor
	 */
	private SpringConfigurationSource(Environment env) {
		this.env = env;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Object> getProperty(String name) {

		return Optional.ofNullable(env.getProperty(name, Object.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getProperty(String name, Object defaultValue) {

		return getProperty(name).orElse(defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getPropertyAsString(String name) {
		
		
		return Optional.ofNullable(env.getProperty(name, String.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPropertyAsString(String name, String defaultValue) {

		return getPropertyAsString(name).orElse(defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Byte> getPropertyAsByte(String name) {

		return Optional.ofNullable(env.getProperty(name, Byte.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte getPropertyAsByte(String name, Byte defaultValue) {

		return getPropertyAsByte(name).orElse(defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Integer> getPropertyAsInt(String name) {

		return Optional.ofNullable(env.getProperty(name, Integer.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getPropertyAsInt(String name, Integer defaultValue) {

		return getPropertyAsInt(name).orElse(defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Float> getPropertyAsFloat(String name) {

		return Optional.ofNullable(env.getProperty(name, Float.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Float getPropertyAsFloat(String name, Float defaultValue) {

		return getPropertyAsFloat(name).orElse(defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Double> getPropertyAsDouble(String name) {

		return Optional.ofNullable(env.getProperty(name, Double.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getPropertyAsDouble(String name, Double defaultValue) {

		return getPropertyAsDouble(name).orElse(defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Long> getPropertyAsLong(String name) {

		return Optional.ofNullable(env.getProperty(name, Long.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getPropertyAsLong(String name, Long defaultValue) {

		return getPropertyAsLong(name).orElse(defaultValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Boolean> getPropertyAsBoolean(String name) {

		return Optional.ofNullable(env.getProperty(name, Boolean.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getPropertyAsBoolean(String name, Boolean defaultValue) {

		return getPropertyAsBoolean(name).orElse(defaultValue);
	}

}
