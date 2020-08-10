package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class SingleKnownType extends Base {
    @JsonImplicitTypes
    @JsonSubTypes({@JsonSubTypes.Type(value = FixedReward.class)})
    interface Reward { }

    static class FixedReward implements Reward {
        public int value;
    }

    private final ArrayList<Reward> rewardsExample = new ArrayList<Reward>() {
        {
            add(new FixedReward() {{value=40;}});
            add(new FixedReward() {{value=50;}});
        }
    };

    @Override
    protected ObjectMapper createTestMapper() {
        return super.createTestMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void serialize() throws IOException {
        String json = mapper.writeValueAsString(rewardsExample);

        assertThat(json).isEqualTo("[{\"value\":40},{\"value\":50}]");
    }

    @Test
    public void deserialize() throws IOException {
        String json = "[{\"value\":40},{\"value\":50,\"max\":100}]";

        ArrayList<Reward> rewards = mapper.readValue(json, new TypeReference<ArrayList<Reward>>() {});

        assertThat(rewards).usingRecursiveComparison().isEqualTo(rewardsExample);
    }
}
