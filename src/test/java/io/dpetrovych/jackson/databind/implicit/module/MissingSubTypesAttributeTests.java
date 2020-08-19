package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MissingSubTypesAttributeTests extends BaseCanSerialize<MissingSubTypesAttributeTests.Reward> {
    @JsonImplicitTypes
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
    public void deserialize__fails() {
        String json = formatJsonArray(getExamplesJson());

        Exception exception = assertThrows(JsonMappingException.class, () -> mapper.readValue(json, new TypeReference<Reward[]>() {}));

        assertThat(exception.getMessage()).isEqualTo(
                "@JsonImplicitTypes annotation require @JsonSubTypes for type: " + Reward.class.getName());
    }
}
