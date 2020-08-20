package io.dpetrovych.jackson.databind.implicit.types;

public class TooManyTypesFoundException extends RuntimeException {
    public final Class<?>[] classes;

    public TooManyTypesFoundException(Class<?>[] classes) {
        this.classes = classes;
    }
}
