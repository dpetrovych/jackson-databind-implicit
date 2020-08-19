package io.dpetrovych.jackson.databind.implicit.types;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PropertiesDescriptor<T> {
    @NotNull
    public final Set<String> properties;
    @NotNull
    public final Class<? extends T> type;

    PropertiesDescriptor(@NotNull Collection<String> properties, @NotNull Class<? extends T> type) {
        this.properties = new HashSet<>(properties);
        this.type = type;
    }

    @Override
    public String toString() {
        return "{properties=" + properties + ", beanClass=" + type + "}";
    }
}

