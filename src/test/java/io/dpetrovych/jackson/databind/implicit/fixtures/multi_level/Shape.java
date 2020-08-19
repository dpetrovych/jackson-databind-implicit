package io.dpetrovych.jackson.databind.implicit.fixtures.multi_level;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;

@JsonImplicitTypes
@JsonSubTypes({
    @JsonSubTypes.Type(value = Circle.class),
    @JsonSubTypes.Type(value = Disk.class),
    @JsonSubTypes.Type(value = Frame.class),
    @JsonSubTypes.Type(value = Rectangle.class),
})
public interface Shape { }

