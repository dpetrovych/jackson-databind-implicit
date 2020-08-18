package io.dpetrovych.jackson.databind.implicit.types;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class PropertiesDescriptor<T> {
    @NotNull
    public final Set<String> properties;
    @NotNull
    public final Class<? extends T> beanClass;

    public PropertiesDescriptor(@NotNull Collection<String> properties, @NotNull Class<? extends T> beanClass) {
        this.properties = new HashSet<>(properties);
        this.beanClass = beanClass;
    }

    @Override
    public String toString() {
        return "{properties=" + properties + ", beanClass=" + beanClass + "}";
    }

    public static <T> PropertiesDescriptor<T> from(BeanDescription beanDescription) {
        Set<String> properties = beanDescription.findProperties().stream()
                .map(BeanPropertyDefinition::getName)
                .collect(Collectors.toSet());

        @SuppressWarnings("unchecked")
        Class<? extends T> beanClass = (Class<? extends T>)beanDescription.getBeanClass();

        return new PropertiesDescriptor<>(properties, beanClass);
    }
}
