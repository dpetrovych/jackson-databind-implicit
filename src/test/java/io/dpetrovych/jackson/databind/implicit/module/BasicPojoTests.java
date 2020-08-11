package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicPojoTests extends BaseCanDeserialize<BasicPojoTests.Reward> {
    @Override
    protected TypeReference<Reward[]> deserializeType() {
        return new TypeReference<Reward[]>() {};
    }

    @JsonImplicitTypes
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FixedReward.class),
            @JsonSubTypes.Type(value = VariableReward.class)})
    interface Reward { }

    static class FixedReward implements Reward {
        public int value;
    }

    static class VariableReward implements Reward {
        public int min, max;
    }

    @Override
    protected Reward[] getExamples() {
        return new Reward[]{
                new FixedReward() {{
                    value = 40;
                }},
                new VariableReward() {{
                    min = 35;
                    max = 45;
                }},
        };
    }

    @Override
    protected String[] getExamplesJson() {
        return new String[]{
                "{\"value\":40}",
                "{\"min\":35,\"max\":45}"
        };
    }

    @Test
    public void deserialize__no_type_found__fails() throws IOException {
        String json = "{\"unknown\":\"unknown\"}";

        Exception exception = assertThrows(JsonMappingException.class, () -> mapper.readValue(json, new TypeReference<Reward>() {}));

        assertThat(exception.getMessage()).isEqualTo(
                "No types matches the object\n" +
                " at [Source: (String)\"{\"unknown\":\"unknown\"}\"; line: 1, column: 21]");
    }
}
