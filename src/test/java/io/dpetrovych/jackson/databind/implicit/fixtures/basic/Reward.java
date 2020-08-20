package io.dpetrovych.jackson.databind.implicit.fixtures.basic;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;

@JsonImplicitTypes
@JsonSubTypes({
    @JsonSubTypes.Type(value = FixedReward.class),
    @JsonSubTypes.Type(value = VariableReward.class)})
public interface Reward { }

