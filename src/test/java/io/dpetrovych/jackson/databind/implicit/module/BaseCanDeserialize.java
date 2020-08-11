package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseCanDeserialize<T> extends BaseCanSerialize<T> {
    protected abstract TypeReference<T[]> deserializeType();

    @Test
    public void deserialize() throws IOException {
        T[] examples = mapper.readValue(getExampleJsonArray(), deserializeType());

        assertThat(examples).usingRecursiveComparison().isEqualTo(getExamples());
    }
}
