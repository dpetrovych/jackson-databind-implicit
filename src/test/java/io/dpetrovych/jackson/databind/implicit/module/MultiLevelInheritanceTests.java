package io.dpetrovych.jackson.databind.implicit.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dpetrovych.jackson.databind.implicit.JsonImplicitTypes;

public class MultiLevelInheritanceTests extends BaseCanDeserialize<MultiLevelInheritanceTests.Shape> {
    @Override
    protected TypeReference<Shape[]> deserializeType() {
        return new TypeReference<Shape[]>() {};
    }

    @JsonImplicitTypes
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Circle.class),
            @JsonSubTypes.Type(value = Disk.class),
            @JsonSubTypes.Type(value = Frame.class),
            @JsonSubTypes.Type(value = Rectangle.class),
    })

    interface Shape { }

    static class Circle implements Shape {
        public int radius;
    }

    static class Disk extends Circle {
        public String fill;
    }

    static class Frame implements Shape {
        public int height, width;
    }

    static class Rectangle extends Frame {
        public String fill;
    }


    @Override
    protected final Shape[] getExamples() {
        return new Shape[] {
            new Circle() {{radius=1;}},
            new Disk() {{radius=1; fill="#FF0000";}},
            new Frame() {{height=1; width=2;}},
            new Rectangle() {{height=1; width=2; fill="#00FF00";}},
        };
    }


    @Override
    protected String[] getExamplesJson() {
        return new String[]{
            "{\"radius\":1}",
            "{\"radius\":1,\"fill\":\"#FF0000\"}",
            "{\"height\":1,\"width\":2}",
            "{\"height\":1,\"width\":2,\"fill\":\"#00FF00\"}",
        };
    }

    @Override
    protected ObjectMapper createTestMapper() {
        return super.createTestMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
