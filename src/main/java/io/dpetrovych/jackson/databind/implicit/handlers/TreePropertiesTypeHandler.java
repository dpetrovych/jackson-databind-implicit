package io.dpetrovych.jackson.databind.implicit.handlers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dpetrovych.jackson.databind.implicit.types.PropertiesDescriptor;
import io.dpetrovych.jackson.databind.implicit.types.TypeSearchNode;
import io.dpetrovych.jackson.databind.implicit.types.TooManyTypesFoundException;
import io.dpetrovych.jackson.databind.implicit.types.TypeSearchTreeBuilder;

import java.util.Collection;
import java.util.Set;


import static io.dpetrovych.jackson.databind.implicit.ErrorMessages.createNoTypesMessage;
import static io.dpetrovych.jackson.databind.implicit.ErrorMessages.createTooManyTypesMessage;
import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.fromIterable;

public class TreePropertiesTypeHandler<T> implements TypeHandler<T> {
    private final TypeSearchNode<T> typeSearchTree;
    private final JavaType superType;
    private final DeserializationConfig config;

    public TreePropertiesTypeHandler(Collection<PropertiesDescriptor<T>> propertiesDescriptors, JavaType superType, DeserializationConfig config) {
        this.superType = superType;
        this.config = config;
        this.typeSearchTree = buildSearchTree(propertiesDescriptors, superType);
    }

    @Override
    public Class<? extends T> getTypeToCast(JsonParser parser, ObjectNode node) throws JsonProcessingException {
        Set<String> nodeFields = fromIterable(node.fieldNames());
        boolean ingoreUnknownProperties = !config.hasDeserializationFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES.getMask());

        try {
            return this.typeSearchTree.find(nodeFields, ingoreUnknownProperties)
                .map(it -> it.node.descriptor.beanClass)
                .orElseThrow(() -> InvalidDefinitionException.from(parser, createNoTypesMessage(), superType));
        } catch (TooManyTypesFoundException e) {
            throw InvalidDefinitionException.from(parser, createTooManyTypesMessage(e.classes), superType);
        }
    }

    private TypeSearchNode<T> buildSearchTree(Collection<PropertiesDescriptor<T>> propertiesDescriptors, JavaType superType) {
        @SuppressWarnings("unchecked")
        TypeSearchTreeBuilder<T> typeTreeBuilder =
            new TypeSearchTreeBuilder<>(propertiesDescriptors, (Class<T>)superType.getRawClass());

        return typeTreeBuilder.build();
    }


}
