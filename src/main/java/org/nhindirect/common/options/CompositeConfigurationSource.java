package org.nhindirect.common.options;

import java.util.List;
import java.util.Optional;

/**
 * A ConfigurationSource composed of multiple internal ConfigurationSources
 * The return value will be retrieved from the first ConfigurationSource that contains
 * the request property.  The search ordering is dependent on the provided List implementation that 
 * contains the ConfigurationSources
 */
public class CompositeConfigurationSource implements ConfigurationSource {

	
	private final List<ConfigurationSource> sources;

	/**
	 * Creates a composite source from a list ConfigurationSources.
	 * @param sources The list of ConfigurationSources.
	 * @return A new CompositeConfigurationSource
	 */
	public static CompositeConfigurationSource of(List<ConfigurationSource> sources) {
		return new CompositeConfigurationSource(sources);
	}
	
	/*
	 * Private constructor
	 */
	private CompositeConfigurationSource(List<ConfigurationSource> sources) {
		this.sources = sources;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Object> getProperty(String name) {
 
		return sources.stream().map(source -> source.getProperty(name))
		   .filter(optional -> optional.isPresent())
		   .findFirst().get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getPropertyAsString(String name) {
		
		return sources.stream().map(source -> source.getPropertyAsString(name))
				   .filter(optional -> optional.isPresent())
				   .findFirst().get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Byte> getPropertyAsByte(String name) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Integer> getPropertyAsInt(String name) {

		return sources.stream().map(source -> source.getPropertyAsInt(name))
				   .filter(optional -> optional.isPresent())
				   .findFirst().get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Float> getPropertyAsFloat(String name) {

		return sources.stream().map(source -> source.getPropertyAsFloat(name))
				   .filter(optional -> optional.isPresent())
				   .findFirst().get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Double> getPropertyAsDouble(String name) {
		
		return sources.stream().map(source -> source.getPropertyAsDouble(name))
				   .filter(optional -> optional.isPresent())
				   .findFirst().get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Long> getPropertyAsLong(String name) {

		return sources.stream().map(source -> source.getPropertyAsLong(name))
				   .filter(optional -> optional.isPresent())
				   .findFirst().get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Boolean> getPropertyAsBoolean(String name) {

		return sources.stream().map(source -> source.getPropertyAsBoolean(name))
				   .filter(optional -> optional.isPresent())
				   .findFirst().get();
	}
	
	
}
