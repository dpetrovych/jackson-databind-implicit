package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.dpetrovych.jackson.databind.implicit.ImplicitPolymorphismTypeHandleModule;

public abstract class Base {
    protected final ObjectMapper mapper = createTestMapper();

    protected ObjectMapper createTestMapper() {
        return new ObjectMapper()
                .registerModule(new ImplicitPolymorphismTypeHandleModule())
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }
}
