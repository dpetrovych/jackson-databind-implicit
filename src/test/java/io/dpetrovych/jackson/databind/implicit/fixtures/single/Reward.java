package io.dpetrovych.jackson.databind.implicit.fixtures.single;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;

@JsonImplicitTypes
@JsonSubTypes({@JsonSubTypes.Type(value = VariableReward.class)})
public interface Reward { }

