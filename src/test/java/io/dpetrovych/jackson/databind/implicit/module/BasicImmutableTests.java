package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;

public class BasicImmutableTests extends BaseCanDeserialize<BasicImmutableTests.Reward> {
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

    @Override
    protected Reward[] getExamples() {
        return new Reward[]{
                new FixedReward(40),
                new VariableReward(35, 45),
        };
    }

    @Override
    protected String[] getExamplesJson() {
        return new String[]{
                "{\"value\":40}",
                "{\"min\":35,\"max\":45}"
        };
    }
}
