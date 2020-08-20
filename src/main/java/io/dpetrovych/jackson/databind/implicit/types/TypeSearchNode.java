package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.*;
import static java.util.stream.Collectors.toList;

public class TypeSearchNode<T> {
    @NotNull
    public final PropertiesDescriptor<? extends T> descriptor;
    @Nullable
    private final List<TypeSearchNode<T>> children;

    public TypeSearchNode(@NotNull PropertiesDescriptor<? extends T> descriptor, @Nullable List<TypeSearchNode<T>> children) {
        if (!descriptor.isSubType() && children == null)
            throw new IllegalArgumentException("Non SubTypeDescriptor nodes shall have children");

        this.children = children;
        this.descriptor = descriptor;
    }

    public Optional<PropertiesDescriptor<? extends T>> find(@NotNull Collection<String> fields) throws TooManyTypesFoundException {
        return find(fields, false);
    }

    public Optional<PropertiesDescriptor<? extends T>> find(@NotNull Collection<String> fields, boolean ignoreUnknownFields) throws TooManyTypesFoundException {
        return findRecursive(fields, ignoreUnknownFields).getDescriptor();
    }

    private TypeSearchResult<T> findRecursive(@NotNull Collection<String> fields, boolean ignoreUnknownFields) throws TooManyTypesFoundException {
        final Set<String> distinctFields = subtract(fields, this.descriptor.properties);

        if (this.descriptor.isSubType())
            return findCandidateChild(distinctFields)
                .map(child -> child.findRecursive(distinctFields, ignoreUnknownFields))
                .orElseGet(() -> (distinctFields.isEmpty() || ignoreUnknownFields)
                    ? TypeSearchResult.of(this.descriptor)
                    : TypeSearchResult.noResult());

        // else if node is not a configured subType (children >= 1)
        if (children.size() == 1)
            return children.get(0).findRecursive(distinctFields, ignoreUnknownFields);

        // children > 1
        return findCandidateChild(distinctFields)
            .map(child -> child.findRecursive(distinctFields, ignoreUnknownFields))
            .orElseThrow(() -> new TooManyTypesFoundException(getNodesClasses(children)));
    }

    @NotNull
    private Optional<TypeSearchNode<T>> findCandidateChild(Set<String> distinctFields) {
        if (distinctFields.isEmpty() || children == null)
            return Optional.empty();

        List<TypeSearchNode<T>> childrenIntersects = children.stream()
            .filter(child -> hasIntersection(distinctFields, child.descriptor.properties))
            .collect(toList());

        if (childrenIntersects.isEmpty())
            return Optional.empty();

        if (childrenIntersects.size() == 1)
            return Optional.of(childrenIntersects.get(0));

        throw new TooManyTypesFoundException(getNodesClasses(childrenIntersects));
    }

    List<TypeSearchNode<T>> getChildren() {
        return this.children != null
            ? new ArrayList<>(this.children)
            : Collections.emptyList();
    }

    List<String> getProperties() {
        return new ArrayList<>(this.descriptor.properties);
    }

    private static <T> Class<?>[] getNodesClasses(List<TypeSearchNode<T>> nodes) {
        return nodes.stream().map(it -> it.descriptor.type).toArray(Class[]::new);
    }
}
