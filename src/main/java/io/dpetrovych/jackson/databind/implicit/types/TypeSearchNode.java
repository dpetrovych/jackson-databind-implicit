package io.dpetrovych.jackson.databind.implicit.types;

import io.dpetrovych.jackson.databind.implicit.helpers.SetHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.subtract;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.partitioningBy;
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

    private Optional<FindTypeResult> findRecursive(@NotNull Collection<String> fields, boolean ignoreUnknownFields) throws TooManyTypesFoundException {
        final Set<String> distinctFields = subtract(fields, this.properties);

        if (distinctFields.isEmpty() && this.descriptor != null)
            return of(new FindTypeResult(this, emptySet()));

        if (children == null)
            return of(new FindTypeResult(this, distinctFields));

        List<FindTypeResult> childrenResult = children.stream()
                .map(it -> it.findRecursive(distinctFields, ignoreUnknownFields))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        Map<Boolean, List<FindTypeResult>> isEquivalentResult = childrenResult.stream().collect(partitioningBy(it -> it.unknownFields.isEmpty()));

        Optional<FindTypeResult> equivalentResult = getSingleResult(isEquivalentResult.get(true));
        if (equivalentResult.isPresent())
            return equivalentResult;

        if (ignoreUnknownFields)
            return getSingleResult(isEquivalentResult.get(false));

        return empty();
    }

    private Optional<FindTypeResult> getSingleResult(List<FindTypeResult> results) throws TooManyTypesFoundException {
        if (results.isEmpty()) return empty();
        if (results.size() == 1) return of(results.get(0));
        throw new TooManyTypesFoundException(results.stream().map(it -> it.node.descriptor.beanClass).collect(toList()));
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
        private final Set<String> unknownFields;

        public FindTypeResult(TypeSearchNode<T> node, Set<String> unknownFields) {
            this.node = node;
            this.unknownFields = unknownFields;
        }
    }
}
