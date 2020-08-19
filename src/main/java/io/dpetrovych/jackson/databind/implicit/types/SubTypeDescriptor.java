package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class SubTypeDescriptor<T> extends PropertiesDescriptor<T> {
    private SubTypeDescriptor(@NotNull Collection<String> properties, @NotNull Class<? extends T> type) {
        super(properties, type);
    }

    public static <T> SubTypeDescriptor<? extends T> from(PropertiesDescriptor<? extends T> descriptor) {
        return new SubTypeDescriptor<>(descriptor.properties, descriptor.type);
    }
}
