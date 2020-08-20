package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.*;
import static io.dpetrovych.jackson.databind.implicit.types.TypeSearchResult.*;
import static java.util.stream.Collectors.toList;

public class TypeSearchNode<T> {
    @NotNull
    final PropertiesDescriptor<? extends T> descriptor;
    @NotNull
    final List<TypeSearchNode<T>> children;

    public TypeSearchNode(@NotNull PropertiesDescriptor<? extends T> descriptor, @Nullable List<TypeSearchNode<T>> children) {
        if (!descriptor.isSubType() && children == null)
            throw new IllegalArgumentException("Non SubTypeDescriptor nodes shall have children");

        this.children = children != null ? children : Collections.emptyList();
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
            return findThroughChildren(distinctFields, ignoreUnknownFields)
                .orElseGet(() -> (distinctFields.isEmpty() || ignoreUnknownFields) ? success(this.descriptor) : noResult());

        // else if node is not a configured subType (children >= 1)
        if (children.size() == 1)
            return children.get(0).findRecursive(distinctFields, ignoreUnknownFields);

        // children > 1
        return findThroughChildren(distinctFields, ignoreUnknownFields)
            .orElseGet(() -> inconclusive(children.stream().map(child -> child.descriptor)));
    }

    @NotNull
    private Optional<TypeSearchResult<T>> findThroughChildren(Set<String> distinctFields, boolean ignoreUnknownFields) {
        if (distinctFields.isEmpty() || children.isEmpty())
            return Optional.empty();

        List<TypeSearchNode<T>> childrenIntersects = children.stream()
            .filter(child -> hasIntersection(distinctFields, child.descriptor.properties))
            .collect(toList());

        if (childrenIntersects.isEmpty())
            return Optional.empty();

        if (childrenIntersects.size() == 1)
            return Optional.of(childrenIntersects.get(0).findRecursive(distinctFields, ignoreUnknownFields));

        return Optional.of(inconclusive(childrenIntersects.stream().map(child -> child.descriptor)));
    }
}
