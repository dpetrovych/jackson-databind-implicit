package io.dpetrovych.jackson.databind.implicit.types;

import io.dpetrovych.jackson.databind.implicit.fixtures.basic.*;
import io.dpetrovych.jackson.databind.implicit.fixtures.multi_level.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.dpetrovych.jackson.databind.implicit.helpers.TestDescriptors.descriptorOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TypeSearchNodeTests {
    @Nested
    public class Single {
        private final TypeSearchNode<Reward> tree = new TypeSearchTreeBuilder<>(
            asList(
                descriptorOf(FixedReward.class)
            ), Reward.class).build();

        @Test
        void emptyFields__finds() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(emptySet());

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(FixedReward.class));
        }

        @Test
        void unknownField__dontIgnore__empty() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(asList("unknown"));

            assertThat(result).isEmpty();
        }

        @Test
        void unknownField__ignore__finds() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(asList("unknown"), true);

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(FixedReward.class));
        }

        @Test
        void valueField__finds() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(asList("value"));

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(FixedReward.class));
        }

        @Test
        void valueAndUnknownField__ignore__finds() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(asList("value", "unknown"), true);

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(FixedReward.class));
        }
    }

    @Nested
    public class Basic {
        private final TypeSearchNode<Reward> tree = new TypeSearchTreeBuilder<>(
            asList(
                descriptorOf(FixedReward.class),
                descriptorOf(VariableReward.class)
            ), Reward.class).build();

        @Test
        void emptyFields__throws() {
            TooManyTypesFoundException exception = assertThrows(TooManyTypesFoundException.class, () -> tree.find(emptySet()));

            assertThat(exception.classes).contains(FixedReward.class, VariableReward.class);
        }

        @Test
        void oneField__find() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(asList("value"));

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(FixedReward.class));
        }

        @Test
        void oneField__missing__empty() {
            TooManyTypesFoundException exception = assertThrows(TooManyTypesFoundException.class, () -> tree.find(asList("unknown")));

            assertThat(exception.classes).contains(FixedReward.class, VariableReward.class);
        }

        @Test
        void oneField__notComplete__find() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(asList("min"));

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(VariableReward.class));
        }

        @Test
        void partialAndUnknown__empty() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(asList("min", "unknown"));

            assertThat(result).isEmpty();
        }

        @Test
        void partialAndUnknown__ignore__find() {
            Optional<PropertiesDescriptor<? extends Reward>> result = tree.find(asList("min", "unknown"), true);

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(VariableReward.class));
        }
    }

    @Nested
    public class Complex {
        private final TypeSearchNode<Shape> tree = new TypeSearchTreeBuilder<>(
            asList(
                descriptorOf(Circle.class),
                descriptorOf(Disk.class),
                descriptorOf(Frame.class),
                descriptorOf(Rectangle.class)
            ), Shape.class).build();

        @Test
        void emptyFields__throws() {
            TooManyTypesFoundException exception = assertThrows(TooManyTypesFoundException.class, () -> tree.find(emptySet()));

            assertThat(exception.classes).contains(Circle.class, Frame.class);
        }

        @Test
        void ambiguousField__fail() {
            TooManyTypesFoundException exception = assertThrows(TooManyTypesFoundException.class, () -> tree.find(asList("fill")));

            assertThat(exception.classes).contains(Circle.class, Frame.class);
        }

        @Test
        void ambiguousField__ignoreUnknown__fail() {
            TooManyTypesFoundException exception = assertThrows(TooManyTypesFoundException.class, () -> tree.find(asList("fill"), true));

            assertThat(exception.classes).contains(Circle.class, Frame.class);
        }

        @Test
        void firstLevelPartial__find() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("height"));

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(Frame.class));
        }

        @Test
        void secondLevelPartial__find() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("width", "fill"));

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(Rectangle.class));
        }

        @Test
        void secondLevelPartial__unknown__empty() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("height", "fill", "unknown"));

            assertThat(result).isEmpty();
        }

        @Test
        void secondLevelPartial__ignoreUnknown__find() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("height", "fill", "unknown"), true);

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(Rectangle.class));
        }


        @Test
        void firstLevel__find() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("radius"));

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(Circle.class));
        }


        @Test
        void firstLevel__unknown__find() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("radius", "unknown"));

            assertThat(result).isEmpty();
        }

        @Test
        void firstLevel__allowUnknown__find() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("radius", "unknown"), true);

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(Circle.class));
        }

        @Test
        void secondLevel__find() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("radius", "fill"));

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(Disk.class));
        }


        @Test
        void secondLevel__unknown__empty() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("radius", "fill", "unknown"));

            assertThat(result).isEmpty();
        }

        @Test
        void secondLevel__ignoreUnknown__find() {
            Optional<PropertiesDescriptor<? extends Shape>> result = tree.find(asList("radius", "fill", "unknown"), true);

            assertThat(result)
                .isPresent()
                .hasValueSatisfying(type -> assertThat(type.beanClass).isEqualTo(Disk.class));
        }
    }
}
