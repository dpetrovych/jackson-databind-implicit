package io.dpetrovych.jackson.databind.implicit.types;

import io.dpetrovych.jackson.databind.implicit.fixtures.basic.*;
import io.dpetrovych.jackson.databind.implicit.fixtures.multi_level.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeTreeBuilderTests {
    @Test
    void simpleType_single_build() {
        TypeSearchNode<Reward> tree = new TypeSearchTreeBuilder<>(asList(
                new PropertiesDescriptor<>(asList("min", "max"), MinMaxReward.class)
        ), Reward.class).build();

        assertThat(tree.getProperties()).isEmpty();
        assertThat(tree.descriptor).isNull();
        List<TypeSearchNode<Reward>> children = tree.getChildren();
        assertThat(children.size()).isEqualTo(1);

        assertThat(children).hasOnlyOneElementSatisfying(node -> {
            assertThat(node.descriptor.beanClass).isEqualTo(MinMaxReward.class);
            assertThat(node.getChildren()).isEmpty();
            assertThat(node.getProperties()).contains("min", "max");
        });
    }

    @Test
    void simpleType_build() {
        TypeSearchNode<Reward> tree = new TypeSearchTreeBuilder<>(asList(
                new PropertiesDescriptor<>(asList("value"), FixedReward.class),
                new PropertiesDescriptor<>(asList("min", "max"), MinMaxReward.class)
        ), Reward.class).build();

        assertThat(tree.getProperties()).isEmpty();
        assertThat(tree.descriptor).isNull();
        List<TypeSearchNode<Reward>> children = tree.getChildren();
        assertThat(children.size()).isEqualTo(2);

        assertThat(children)
            .filteredOn(node -> node.descriptor.beanClass.equals(MinMaxReward.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.getChildren()).isEmpty();
                assertThat(node.getProperties()).contains("min", "max");
            });

        assertThat(children)
            .filteredOn(node -> node.descriptor.beanClass.equals(FixedReward.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.getChildren()).isEmpty();
                assertThat(node.getProperties()).contains("value");
            });
    }

    @Test
    void complexType__build() {
        TypeSearchNode<Shape> tree = new TypeSearchTreeBuilder<>(asList(
                new PropertiesDescriptor<>(asList("radius"), Circle.class),
                new PropertiesDescriptor<>(asList("radius", "fill"), Disk.class),
                new PropertiesDescriptor<>(asList("height", "width"), Frame.class),
                new PropertiesDescriptor<>(asList("height", "width", "fill"), Rectangle.class)
        ), Shape.class).build();

        List<TypeSearchNode<Shape>> children = tree.getChildren();

        assertThat(children).hasSize(2);
        assertThat(children)
            .filteredOn(node -> node.descriptor.beanClass.equals(Frame.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.getProperties()).contains("height", "width");
                assertThat(node.getChildren().size()).isEqualTo(1);
                assertThat(node.getChildren().get(0).descriptor.beanClass).isEqualTo(Rectangle.class);
            });

        assertThat(children)
            .filteredOn(node -> node.descriptor.beanClass.equals(Circle.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.getProperties()).contains("radius");
                assertThat(node.getChildren().size()).isEqualTo(1);
                assertThat(node.getChildren().get(0).descriptor.beanClass).isEqualTo(Disk.class);
            });
    }

    @Test
    void complexType_skipDescriptors_buildsFullTree() {
        TypeSearchNode<Shape> tree = new TypeSearchTreeBuilder<>(asList(
                new PropertiesDescriptor<>(asList("radius", "fill"), Disk.class),
                new PropertiesDescriptor<>(asList("height", "width", "fill"), Rectangle.class)
        ), Shape.class).build();

        List<TypeSearchNode<Shape>> children = tree.getChildren();
        assertThat(children).hasSize(2).allSatisfy(node -> {
            assertThat(node.descriptor).isNull();
            assertThat(node.getProperties()).isEmpty();
            assertThat(node.getChildren().size()).isEqualTo(1);
        });

        assertThat(children)
            .filteredOn(node -> node.getChildren().get(0).descriptor.beanClass.equals(Disk.class))
            .hasSize(1);

        assertThat(children)
            .filteredOn(node -> node.getChildren().get(0).descriptor.beanClass.equals(Rectangle.class))
            .hasSize(1);
    }
}
