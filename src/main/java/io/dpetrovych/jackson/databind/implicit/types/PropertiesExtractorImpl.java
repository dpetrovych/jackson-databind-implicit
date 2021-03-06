package io.dpetrovych.jackson.databind.implicit.types;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class PropertiesExtractorImpl implements PropertiesExtractor {
    private final DeserializationConfig config;

    public PropertiesExtractorImpl(DeserializationConfig config) {
        this.config = config;
    }

    @Override
    @NotNull
    public <T> PropertiesDescriptor<? extends T> getPropertiesDescriptor(Class<?> type) {
        JavaType javaType = config.getTypeFactory().constructType(type);
        BeanDescription beanDescription = this.config.introspect(javaType);
        //noinspection unchecked
        return new PropertiesDescriptor<>(getProperties(beanDescription), (Class<? extends T>)type);
    }

    @NotNull
    protected static Set<String> getProperties(BeanDescription beanDescription) {
        return beanDescription.findProperties().stream()
            .map(BeanPropertyDefinition::getName)
            .collect(Collectors.toSet());
    }
}
