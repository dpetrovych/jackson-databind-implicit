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
    @JsonSubTypes({@JsonSubTypes.Type(value = VariableReward.class)})
    interface Reward { }

    static class VariableReward implements Reward {
        public int min,max;
    }

    @Override
    protected Reward[] getExamples() {
        return new Reward[]{
                new VariableReward() {{
                    min = 40;
                    max = 50;
                }},
                new VariableReward() {{
                    min = 50;
                    max = 60;
                }},
        };
    }

    @Override
    protected String[] getExamplesJson() {
        return new String[]{
                "{\"min\":40,\"max\":50}",
                "{\"min\":50,\"max\":60}",
        };
    }

    @Override
    protected ObjectMapper createTestMapper() {
        return super.createTestMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Test
    public void deserialize__with_unknown_props__success() throws IOException {
        String json = "{\"min\":40,\"max\":50,\"plus\":true}";

        Reward reward = mapper.readValue(json, new TypeReference<Reward>() {});

        assertThat(reward).usingRecursiveComparison().isEqualTo(new VariableReward() {{ min=40; max=50; }});
    }

    @Test
    public void deserialize__with_incomplete_props__success() throws IOException {
        String json = "{\"max\":50}";

        Reward reward = mapper.readValue(json, new TypeReference<Reward>() {});

        assertThat(reward).usingRecursiveComparison().isEqualTo(new VariableReward() {{ min=0; max=50; }});
    }
}
