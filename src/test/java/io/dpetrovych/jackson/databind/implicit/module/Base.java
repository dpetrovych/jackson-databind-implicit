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

    protected String formatJsonArray(String... examples) {
        if (examples == null)
            return null;

        int iMax = examples.length - 1;
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; ; i++) {
            builder.append(examples[i]);
            if (i == iMax)
                return builder.append(']').toString();
            builder.append(",");
        }
    }
}

