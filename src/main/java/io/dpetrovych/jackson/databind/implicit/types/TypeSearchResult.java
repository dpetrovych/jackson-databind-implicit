package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

class TypeSearchResult<T> {
    private static TypeSearchResult<Object> EMPTY = new TypeSearchResult<>(null);

    private final PropertiesDescriptor<? extends T> descriptor;

    private TypeSearchResult(PropertiesDescriptor<? extends T> descriptor) {
        this.descriptor = descriptor;
    }

    Optional<PropertiesDescriptor<? extends T>> getDescriptor() {
        return Optional.ofNullable(descriptor);
    }

    public static <T> TypeSearchResult<T> of(@NotNull PropertiesDescriptor<? extends T> descriptor) {
        return new TypeSearchResult<>(descriptor);
    }

    public static <T> TypeSearchResult<T> noResult() {
        //noinspection unchecked
        return (TypeSearchResult<T>)EMPTY;
    }
}
