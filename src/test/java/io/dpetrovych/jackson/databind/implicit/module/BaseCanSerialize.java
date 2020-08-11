package io.dpetrovych.jackson.databind.implicit.module;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseCanSerialize<T> extends Base {
    protected abstract T[] getExamples();

    protected abstract String[] getExamplesJson();

    @Test
    public void serialize() throws IOException {
        String json = mapper.writeValueAsString(getExamples());

        assertThat(json).isEqualTo(getExampleJsonArray());
    }

    protected String getExampleJsonArray() {
        String[] examples = getExamplesJson();
        return formatJsonArray(examples);
    }
}


