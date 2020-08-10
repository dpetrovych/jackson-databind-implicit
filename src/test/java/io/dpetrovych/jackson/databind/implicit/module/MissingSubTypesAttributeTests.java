package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MissingSubTypesAttributeTests extends Base {
    @JsonImplicitTypes
    interface Reward { }

    static class FixedReward implements Reward {
        public int value;
    }

    static class VariableReward implements Reward {
        public int min, max;
    }

    private final ArrayList<Reward> rewardsExample = new ArrayList<Reward>() {
        {
            add(new FixedReward() {{value=40;}});
            add(new VariableReward() {{min=35; max=45;}});
        }
    };

    @Test
    public void serialize() throws IOException {
        String json = mapper.writeValueAsString(rewardsExample);

        assertThat(json).isEqualTo("[{\"value\":40},{\"min\":35,\"max\":45}]");
    }

    @Test
    public void deserialize__fails() {
        String json = "[{\"value\":40},{\"min\":35,\"max\":45}]";

        Exception exception = assertThrows(JsonMappingException.class, () -> mapper.readValue(json, new TypeReference<ArrayList<Reward>>() {}));

        assertThat(exception.getMessage()).isEqualTo(
                "@JsonImplicitTypes annotation require @JsonSubTypes for type: " + Reward.class.getName());
    }
}
