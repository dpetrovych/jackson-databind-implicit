package io.dpetrovych.jackson.databind.implicit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultPolymorphicTypeHandlingTests {
    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, property="type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FixedReward.class, name = "fixed"),
            @JsonSubTypes.Type(value = VariableReward.class, name = "variable")
    })
    interface Reward {
    }

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
        private final int min;
        private final int max;

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

    private final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    private final ArrayList<Reward> rewardsExample = new ArrayList<Reward>() {
        {
            add(new FixedReward(40));
            add(new VariableReward(35, 45));
        }
    };

    @Test
    public void serialize() throws IOException {
        String json = mapper.writeValueAsString(rewardsExample);

        assertThat(json).isEqualTo(
                "[{\"type\":\"fixed\",\"value\":40}," +
                 "{\"type\":\"variable\",\"min\":35,\"max\":45}]");
    }

    @Test
    public void deserialize() throws IOException {
        String json = "[{\"type\":\"fixed\",\"value\":40}," +
                       "{\"type\":\"variable\",\"min\":35,\"max\":45}]";

        ArrayList<Reward> rewards = mapper.readValue(json, new TypeReference<ArrayList<Reward>>() {});

        assertThat(rewards).usingRecursiveComparison().isEqualTo(rewardsExample);
    }
}
