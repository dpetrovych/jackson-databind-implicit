package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicImmutableTests extends Base {
    @JsonImplicitTypes
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FixedReward.class),
            @JsonSubTypes.Type(value = VariableReward.class)})
    interface Reward { }

    static class FixedReward implements Reward {
        private final int value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public FixedReward(@JsonProperty("value") int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    static class VariableReward implements Reward {
        private final int min, max;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public VariableReward(@JsonProperty("min") int min, @JsonProperty("max") int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

    private final ArrayList<Reward> rewardsExample = new ArrayList<Reward>() {
        {
            add(new FixedReward(40));
            add(new VariableReward(35, 45));
        }
    };

    @Test
    public void serialize() throws IOException {
        String json = mapper.writeValueAsString(rewardsExample);

        assertThat(json).isEqualTo("[{\"value\":40},{\"min\":35,\"max\":45}]");
    }

    @Test
    public void deserialize() throws IOException {
        String json = "[{\"value\":40},{\"min\":35,\"max\":45}]";

        ArrayList<Reward> rewards = mapper.readValue(json, new TypeReference<ArrayList<Reward>>() {});

        assertThat(rewards).usingRecursiveComparison().isEqualTo(rewardsExample);
    }
}
