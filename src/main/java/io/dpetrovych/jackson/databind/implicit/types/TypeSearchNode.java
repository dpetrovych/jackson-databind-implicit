package io.dpetrovych.jackson.databind.implicit.types;

import io.dpetrovych.jackson.databind.implicit.helpers.SetHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.intersect;
import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.subtract;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class TypeSearchNode<T> {
    public final PropertiesDescriptor<? extends T> descriptor;
    private final Collection<TypeSearchNode<T>> children;
    private final Set<String> properties;

    public TypeSearchNode(PropertiesDescriptor<? extends T> descriptor, Collection<TypeSearchNode<T>> children) {
        if ((children == null || children.isEmpty()) && descriptor == null) {
            throw new IllegalArgumentException("descriptor should be not-null for a terminal node");
        }

        this.children = children;
        this.descriptor = descriptor;
        this.properties = descriptor != null ? descriptor.properties : collectCommonProperties(children);
    }

    private static <T> Set<String> collectCommonProperties(@NotNull Collection<TypeSearchNode<T>> children) {
        if (children.size() == 1)
            return emptySet();

        return children.stream()
                .map(it -> it.properties)
                .reduce(SetHelper::intersect)
                .orElse(emptySet());
    }

    public Optional<PropertiesDescriptor<? extends T>> find(@NotNull Collection<String> fields) throws TooManyTypesFoundException {
        return find(fields, false);
    }

    public Optional<PropertiesDescriptor<? extends T>> find(@NotNull Collection<String> fields, boolean ignoreUnknownFields) throws TooManyTypesFoundException {
        return findRecursive(fields, ignoreUnknownFields).map(it -> it.node.descriptor);
    }

    @SuppressWarnings("MethodComplexity")
    private Optional<FindTypeResult> findRecursive(@NotNull Collection<String> fields, boolean ignoreUnknownFields) throws TooManyTypesFoundException {
        final Set<String> distinctFields = subtract(fields, this.properties);

        if (children != null) {
            List<TypeSearchNode<T>> childrenIntersects = children.stream()
                .filter(child ->
                    (children.size() == 1 && this.descriptor == null) ||
                    (!distinctFields.isEmpty() && intersect(child.properties, distinctFields).size() > 0))
                .collect(toList());

            if (childrenIntersects.size() == 1)
                return childrenIntersects.get(0).findRecursive(distinctFields, ignoreUnknownFields);

            if (childrenIntersects.size() > 1 || descriptor == null)
                throw new TooManyTypesFoundException(children.stream().map(it -> it.descriptor.beanClass).collect(toList()));
        }

        return this.descriptor != null && (distinctFields.isEmpty() || ignoreUnknownFields)
            ? of(new FindTypeResult(this))
            : empty();
    }

    List<TypeSearchNode<T>> getChildren() {
        return this.children != null
            ? new ArrayList<>(this.children)
            : emptyList();
    }

    List<String> getProperties() {
        return new ArrayList<>(this.properties);
    }

    public class FindTypeResult {
        public final TypeSearchNode<T> node;

        public FindTypeResult(TypeSearchNode<T> node) {
            this.node = node;
        }
    }
}
