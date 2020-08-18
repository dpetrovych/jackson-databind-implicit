package io.dpetrovych.jackson.databind.implicit.types;

import java.util.List;

public class TooManyTypesFoundException extends RuntimeException {
    public final List<Class<?>> classes;

    public TooManyTypesFoundException(List<Class<?>> classes) {
        this.classes = classes;
    }
}
