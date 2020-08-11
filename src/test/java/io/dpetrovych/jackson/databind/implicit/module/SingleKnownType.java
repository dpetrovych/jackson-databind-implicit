package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SingleKnownType extends BaseCanDeserialize<SingleKnownType.Reward> {
    @Override
    protected TypeReference<Reward[]> deserializeType() {
        return new TypeReference<Reward[]>() {};
    }

    @JsonImplicitTypes
    @JsonSubTypes({@JsonSubTypes.Type(value = FixedReward.class)})
    interface Reward { }

    static class FixedReward implements Reward {
        public int value;
    }

    @Override
    protected Reward[] getExamples() {
        return new Reward[]{
                new FixedReward() {{
                    value = 40;
                }},
                new FixedReward() {{
                    value = 50;
                }},
        };
    }

    @Override
    protected String[] getExamplesJson() {
        return new String[]{
                "{\"value\":40}",
                "{\"value\":50}",
        };
    }

    @Override
    protected ObjectMapper createTestMapper() {
        return super.createTestMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Test
    public void deserialize__with_unknown_props__success() throws IOException {
        String json = formatJsonArray("{\"value\":40}", "{\"value\":50,\"max\":100}");

        Reward[] rewards = mapper.readValue(json, deserializeType());

        assertThat(rewards).usingRecursiveComparison().isEqualTo(rewards);
    }
}
