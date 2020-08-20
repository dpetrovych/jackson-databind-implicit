package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

abstract class TypeSearchResult<T> {
    abstract Optional<PropertiesDescriptor<? extends T>> getDescriptor();

    public static <T> TypeSearchResult<T> inconclusive(@NotNull Stream<PropertiesDescriptor<? extends T>> descriptors) {
        return new InconclusiveTypeSearchResult<>(descriptors);
    }

    public static <T> TypeSearchResult<T> success(@NotNull PropertiesDescriptor<? extends T> descriptor) {
        return new SuccessTypeSearchResult<>(descriptor);
    }

    public static <T> TypeSearchResult<T> noResult() {
        return new EmptyResult<>();
    }

    static class EmptyResult<T> extends TypeSearchResult<T> {
        @Override
        Optional<PropertiesDescriptor<? extends T>> getDescriptor() {
            return Optional.empty();
        }
    }

    static class SuccessTypeSearchResult<T> extends TypeSearchResult<T> {
        private final PropertiesDescriptor<? extends T> descriptor;

        private SuccessTypeSearchResult(@NotNull PropertiesDescriptor<? extends T> descriptor) {
            this.descriptor = descriptor;
        }

        Optional<PropertiesDescriptor<? extends T>> getDescriptor() {
            return Optional.of(descriptor);
        }
    }

    static class InconclusiveTypeSearchResult<T> extends TypeSearchResult<T> {
        private final Class<?>[] types;

        private InconclusiveTypeSearchResult(@NotNull Stream<PropertiesDescriptor<? extends T>> descriptors) {
            this.types = descriptors.map(it -> it.type).toArray(Class[]::new);
        }

        Optional<PropertiesDescriptor<? extends T>> getDescriptor() {
            throw new TooManyTypesFoundException(types);
        }
    }
}




