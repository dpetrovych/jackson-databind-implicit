package io.dpetrovych.jackson.databind.implicit;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class ErrorMessages {
    public static String createTooManyTypesMessage(@NotNull List<Class<?>> propsToClasses) {
        List<String> typeNames = propsToClasses.stream().map(Class::getName).collect(toList());
        return format("%s types matches the same object: %s", typeNames.size(), typeNames);
    }

    public static String createNoTypesMessage() {
        return "No types matches the object";
    }
}
