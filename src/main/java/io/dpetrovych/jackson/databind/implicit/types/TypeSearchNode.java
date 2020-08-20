package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.*;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class TypeSearchNode<T> {
    public final PropertiesDescriptor<? extends T> descriptor;
    private final Collection<TypeSearchNode<T>> children;

    public TypeSearchNode(@NotNull PropertiesDescriptor<? extends T> descriptor, Collection<TypeSearchNode<T>> children) {
        this.children = children;
        this.descriptor = descriptor;
    }

    public Optional<PropertiesDescriptor<? extends T>> find(@NotNull Collection<String> fields) throws TooManyTypesFoundException {
        return find(fields, false);
    }

    public Optional<PropertiesDescriptor<? extends T>> find(@NotNull Collection<String> fields, boolean ignoreUnknownFields) throws TooManyTypesFoundException {
        return findRecursive(fields, ignoreUnknownFields).map(it -> it.node.descriptor);
    }

    @SuppressWarnings("MethodComplexity")
    private Optional<FindTypeResult> findRecursive(@NotNull Collection<String> fields, boolean ignoreUnknownFields) throws TooManyTypesFoundException {
        final Set<String> distinctFields = subtract(fields, this.descriptor.properties);

        if (children != null) {
            List<TypeSearchNode<T>> childrenIntersects = children.stream()
                .filter(child ->
                    (children.size() == 1 && !(this.descriptor instanceof SubTypeDescriptor)) ||
                    (!distinctFields.isEmpty() && intersect(child.descriptor.properties, distinctFields).size() > 0))
                .collect(toList());

            if (childrenIntersects.size() == 1)
                return childrenIntersects.get(0).findRecursive(distinctFields, ignoreUnknownFields);

            if (childrenIntersects.size() > 1 || !(this.descriptor instanceof SubTypeDescriptor))
                throw new TooManyTypesFoundException(children.stream().map(it -> it.descriptor.type).collect(toList()));
        }

        return this.descriptor instanceof SubTypeDescriptor && (distinctFields.isEmpty() || ignoreUnknownFields)
            ? of(new FindTypeResult(this))
            : empty();
    }

    List<TypeSearchNode<T>> getChildren() {
        return this.children != null
            ? new ArrayList<>(this.children)
            : emptyList();
    }

    List<String> getProperties() {
        return new ArrayList<>(this.descriptor.properties);
    }

    public class FindTypeResult {
        public final TypeSearchNode<T> node;

        public FindTypeResult(TypeSearchNode<T> node) {
            this.node = node;
        }
    }
}
