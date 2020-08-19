package io.dpetrovych.jackson.databind.implicit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class ErrorMessages {
    public static String subtypesAnnotationRequired(@NotNull Class<?> superclass) {
        return format("@%s annotation require @%s for type: %s",
            JsonImplicitTypes.class.getSimpleName(), JsonSubTypes.class.getSimpleName(), superclass.getName());
    }

    public static String onlyObjectDeserializationAllowed(@NotNull Class<?> superclass) {
        return format("Only object node can be deserialized as %s", superclass.getName());
    }

    public static String createTooManyTypesMessage(@NotNull List<Class<?>> propsToClasses) {
        List<String> typeNames = propsToClasses.stream().map(Class::getName).collect(toList());
        return format("%s types matches the same object: %s", typeNames.size(), typeNames);
    }

    public static String createNoTypesMessage() {
        return "No types matches the object";
    }
}
