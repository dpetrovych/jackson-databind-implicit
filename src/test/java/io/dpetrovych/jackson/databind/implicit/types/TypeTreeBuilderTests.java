package io.dpetrovych.jackson.databind.implicit.types;

import io.dpetrovych.jackson.databind.implicit.fixtures.basic.*;
import io.dpetrovych.jackson.databind.implicit.fixtures.multi_level.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeTreeBuilderTests {
    private final PropertiesExtractor propertiesExtractor;

    public TypeTreeBuilderTests() {
        this.propertiesExtractor = new PropertiesExtractorMock();
    }

    @Test
    void simpleType_single_build() {
        TypeSearchNode<Reward> tree = new TypeSearchTreeBuilder<>(new Class[]{
            VariableReward.class
        }, Reward.class, this.propertiesExtractor).build();

        assertThat(tree.descriptor.type).isEqualTo(Reward.class);
        List<TypeSearchNode<Reward>> children = tree.getChildren();
        assertThat(children.size()).isEqualTo(1);

        assertThat(children).hasOnlyOneElementSatisfying(node -> {
            assertThat(node.descriptor.type).isEqualTo(VariableReward.class);
            assertThat(node.getChildren()).isEmpty();
            assertThat(node.getProperties()).contains("min", "max");
        });
    }

    @Test
    void simpleType_build() {
        TypeSearchNode<Reward> tree = new TypeSearchTreeBuilder<>(new Class[]{
            FixedReward.class,
            VariableReward.class
        }, Reward.class, this.propertiesExtractor).build();

        assertThat(tree.descriptor.type).isEqualTo(Reward.class);
        List<TypeSearchNode<Reward>> children = tree.getChildren();
        assertThat(children.size()).isEqualTo(2);

        assertThat(children)
            .filteredOn(node -> node.descriptor.type.equals(VariableReward.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.getChildren()).isEmpty();
                assertThat(node.getProperties()).contains("min", "max");
            });

        assertThat(children)
            .filteredOn(node -> node.descriptor.type.equals(FixedReward.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.getChildren()).isEmpty();
                assertThat(node.getProperties()).contains("value");
            });
    }

    @Test
    void complexType__build() {
        TypeSearchNode<Shape> tree = new TypeSearchTreeBuilder<>(new Class[]{
            Circle.class,
            Disk.class,
            Frame.class,
            Rectangle.class
        }, Shape.class, this.propertiesExtractor).build();

        List<TypeSearchNode<Shape>> children = tree.getChildren();

        assertThat(children).hasSize(2);
        assertThat(children)
            .filteredOn(node -> node.descriptor.type.equals(Frame.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.descriptor).isExactlyInstanceOf(SubTypeDescriptor.class);
                assertThat(node.getProperties()).contains("height", "width");
                assertThat(node.getChildren().size()).isEqualTo(1);
                assertThat(node.getChildren().get(0).descriptor).isExactlyInstanceOf(SubTypeDescriptor.class);
                assertThat(node.getChildren().get(0).descriptor.type).isEqualTo(Rectangle.class);
            });

        assertThat(children)
            .filteredOn(node -> node.descriptor.type.equals(Circle.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.descriptor).isExactlyInstanceOf(SubTypeDescriptor.class);
                assertThat(node.getProperties()).contains("radius");
                assertThat(node.getChildren().size()).isEqualTo(1);
                assertThat(node.getChildren().get(0).descriptor).isExactlyInstanceOf(SubTypeDescriptor.class);
                assertThat(node.getChildren().get(0).descriptor.type).isEqualTo(Disk.class);
            });
    }

    @Test
    void complexType_skipDescriptors_buildsFullTree() {
        TypeSearchNode<Shape> tree = new TypeSearchTreeBuilder<>(new Class[]{
            Disk.class,
            Rectangle.class
        }, Shape.class, this.propertiesExtractor).build();


        List<TypeSearchNode<Shape>> children = tree.getChildren();
        assertThat(children).hasSize(2);
        assertThat(children)
            .filteredOn(node -> node.descriptor.type.equals(Frame.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.descriptor).isExactlyInstanceOf(PropertiesDescriptor.class);
                assertThat(node.getProperties()).contains("height", "width");
                assertThat(node.getChildren().size()).isEqualTo(1);
                assertThat(node.getChildren().get(0).descriptor).isExactlyInstanceOf(SubTypeDescriptor.class);
                assertThat(node.getChildren().get(0).descriptor.type).isEqualTo(Rectangle.class);
            });

        assertThat(children)
            .filteredOn(node -> node.descriptor.type.equals(Circle.class))
            .hasOnlyOneElementSatisfying(node -> {
                assertThat(node.descriptor).isExactlyInstanceOf(PropertiesDescriptor.class);
                assertThat(node.getProperties()).contains("radius");
                assertThat(node.getChildren().size()).isEqualTo(1);
                assertThat(node.getChildren().get(0).descriptor).isExactlyInstanceOf(SubTypeDescriptor.class);
                assertThat(node.getChildren().get(0).descriptor.type).isEqualTo(Disk.class);
            });
    }
}
