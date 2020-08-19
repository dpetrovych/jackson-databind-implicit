package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PropertiesExtractorMock implements PropertiesExtractor {

    @NotNull
    @Override
    public <T> PropertiesDescriptor<? extends T> getPropertiesDescriptor(Class<?> type) {
        List<String> fields = Arrays.stream(type.getDeclaredFields()).map(Field::getName).collect(toList());
        return new PropertiesDescriptor<T>(fields, (Class<? extends T>) type);
    }
}
