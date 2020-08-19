package io.dpetrovych.jackson.databind.implicit.types;

public interface PropertiesExtractor {
    <T> PropertiesDescriptor<? extends T> getPropertiesDescriptor(Class<?> type);
}

