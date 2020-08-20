package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

public interface PropertiesExtractor {
    @NotNull
    <T> PropertiesDescriptor<? extends T> getPropertiesDescriptor(Class<?> type);
}

