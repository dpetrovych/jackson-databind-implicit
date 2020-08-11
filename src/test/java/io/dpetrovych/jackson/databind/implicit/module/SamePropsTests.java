package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamePropsTests extends BaseCanSerialize<SamePropsTests.Reward> {
    @JsonImplicitTypes
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MinMaxReward.class),
            @JsonSubTypes.Type(value = MaxMinReward.class)})
    interface Reward { }

    static class MinMaxReward implements Reward {
        public int max, min;
    }

    static class MaxMinReward implements Reward {
        public int min, max;
    }

    @Override
    protected Reward[] getExamples() {
        return new Reward[]{
                new MinMaxReward() {{
                    min = 40;
                    max = 50;
                }},
                new MaxMinReward() {{
                    max = 45;
                    min = 35;
                }},
        };
    }

    @Override
    protected String[] getExamplesJson() {
        return new String[]{
                "{\"max\":50,\"min\":40}",
                "{\"min\":35,\"max\":45}",
        };
    }

    @Test
    public void deserialize__fails() {
        Exception exception = assertThrows(JsonMappingException.class, () -> mapper.readValue(getExampleJsonArray(), new TypeReference<Reward[]>() {}));

        assertThat(exception.getMessage()).isEqualTo(
                "2 types matches the same object: [" + MinMaxReward.class.getName() + ", "+ MaxMinReward.class.getName() + "]\n" +
                " at [Source: (String)\"[{\"max\":50,\"min\":40},{\"min\":35,\"max\":45}]\"; line: 1, column: 20] (through reference chain: java.lang.Object[][0])");
    }
}
