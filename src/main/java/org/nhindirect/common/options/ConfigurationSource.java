package org.nhindirect.common.options;

import java.util.Optional;

/**
 * Abstraction interface for retrieving information from various source configuration systems
 * such as Spring application context or an Apache James Mailet.
 * 
 * @author gm2552
 */
public interface ConfigurationSource {

	/**
	 * Gets a generic property object value by name.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The optional will be empty if the property is not found in
	 * the underlying source.
	 */
	Optional<Object> getProperty(String name);
	
	/**
	 * Gets a generic property object value by name with a default value.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The default value will be returned if the property is not found in
	 * the underlying source.
	 */
	default Object getProperty(String name, final Object defaultValue) {
		return getProperty(name).orElse(defaultValue);
	}
	
	/**
	 * Gets a String property value by name.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The optional will be empty if the property is not found in
	 * the underlying source.
	 */
	Optional<String> getPropertyAsString(String name);
	
	/**
	 * Gets a String property object value by name with a default value.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The default value will be returned if the property is not found in
	 * the underlying source.
	 */
	default String getPropertyAsString(String name, final String defaultValue) {
		return getPropertyAsString(name).orElse(defaultValue);
	}
	
	/**
	 * Gets a Byte property value by name.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The optional will be empty if the property is not found in
	 * the underlying source.
	 */
	Optional<Byte> getPropertyAsByte(String name);
	
	/**
	 * Gets a Byte property object value by name with a default value.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The default value will be returned if the property is not found in
	 * the underlying source.
	 */
	default Byte getPropertyAsByte(String name, final Byte defaultValue) {
		return getPropertyAsByte(name).orElse(defaultValue);
	}
	
	/**
	 * Gets an Integer property value by name.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The optional will be empty if the property is not found in
	 * the underlying source.
	 */
	Optional<Integer> getPropertyAsInt(String name);
	
	/**
	 * Gets an Integer property object value by name with a default value.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The default value will be returned if the property is not found in
	 * the underlying source.
	 */
	default Integer getPropertyAsInt(String name, final Integer defaultValue) {
		return getPropertyAsInt(name).orElse(defaultValue);
	}
	
	/**
	 * Gets a Float property value by name.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The optional will be empty if the property is not found in
	 * the underlying source.
	 */
	Optional<Float> getPropertyAsFloat(String name);
	
	/**
	 * Gets a Float property object value by name with a default value.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The default value will be returned if the property is not found in
	 * the underlying source.
	 */
	default Float getPropertyAsFloat(String name, final Float defaultValue) {
		return getPropertyAsFloat(name).orElse(defaultValue);
	}
	
	/**
	 * Gets a Double property value by name.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The optional will be empty if the property is not found in
	 * the underlying source.
	 */
	Optional<Double> getPropertyAsDouble(String name);
	
	/**
	 * Gets a Double property object value by name with a default value.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The default value will be returned if the property is not found in
	 * the underlying source.
	 */
	default Double getPropertyAsDouble(String name, final Double defaultValue) {
		return getPropertyAsDouble(name).orElse(defaultValue);
	}
	
	/**
	 * Gets a Long property value by name.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The optional will be empty if the property is not found in
	 * the underlying source.
	 */
	Optional<Long> getPropertyAsLong(String name);
	
	/**
	 * Gets a Long property object value by name with a default value.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The default value will be returned if the property is not found in
	 * the underlying source.
	 */
	default Long getPropertyAsLong(String name, final Long defaultValue) {
		return getPropertyAsLong(name).orElse(defaultValue);
	}
	
	/**
	 * Gets a Boolean property value by name.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The optional will be empty if the property is not found in
	 * the underlying source.
	 */
	Optional<Boolean> getPropertyAsBoolean(String name);
	
	/**
	 * Gets a Boolean property object value by name with a default value.
	 * @param name The name of the property to retrieve.
	 * @return An Optional containing the retrieved property value.  The default value will be returned if the property is not found in
	 * the underlying source.
	 */
	default Boolean getPropertyAsBoolean(String name, final Boolean defaultValue) {
		return getPropertyAsBoolean(name).orElse(defaultValue);
	}
	
}
