package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class SubTypeDescriptor<T> extends PropertiesDescriptor<T> {
    private SubTypeDescriptor(@NotNull Collection<String> properties, @NotNull Class<T> type) {
        super(properties, type);
    }

    @Override
    public boolean isSubType() {
        return true;
    }

    public static <T> SubTypeDescriptor<? extends T> from(PropertiesDescriptor<? extends T> descriptor) {
        return new SubTypeDescriptor<>(descriptor.properties, descriptor.type);
    }
}
