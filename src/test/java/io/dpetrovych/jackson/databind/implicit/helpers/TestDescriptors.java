package io.dpetrovych.jackson.databind.implicit.helpers;


import io.dpetrovych.jackson.databind.implicit.types.PropertiesDescriptor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TestDescriptors {
    public static <T> PropertiesDescriptor<T> descriptorOf(Class<T> cls) {
        List<String> fields = Arrays.stream(cls.getDeclaredFields()).map(Field::getName).collect(toList());
        return new PropertiesDescriptor<T>(fields, cls);
    }
}
