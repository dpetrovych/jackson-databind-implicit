package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.of;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class TypeSearchTreeBuilder<T> {
    private final Class<?>[] types;
    private final Class<T> superclass;
    private final PropertiesExtractor propertiesExtractor;

    public TypeSearchTreeBuilder(Class<?>[] types, Class<T> superclass, PropertiesExtractor propertiesExtractor) {
        this.types = types;
        this.superclass = superclass;
        this.propertiesExtractor = propertiesExtractor;
    }

    public TypeSearchNode<T> build() {
        Stream<SubTypeDescriptor<? extends T>> descriptors = Arrays.stream(types)
            .map(this.propertiesExtractor::<T>getPropertiesDescriptor)
            .map(SubTypeDescriptor::<T>from);

        List<TypeHierarchyNode<T>> classNodes = descriptors.map(this::buildClassPath).collect(Collectors.toList());
        return new TypeSearchNode<>(null, combineClassNodes(classNodes));
    }

    @NotNull
    private TypeHierarchyNode<T> buildClassPath(SubTypeDescriptor<? extends T> descriptor) {
        return buildClassPath(new TypeHierarchyNode<>(descriptor))
            .orElseThrow(() -> new RuntimeException(
                String.format("Class %s should be descendant of %s", descriptor.type.getName(), this.superclass.getName())));
    }

    @NotNull
    private Optional<TypeHierarchyNode<T>> buildClassPath(@NotNull TypeHierarchyNode<T> classNode) {
        Collection<Class<?>> parentTypes = classNode.getParentTypes(this.superclass.isInterface());

        if (parentTypes.stream().anyMatch(this.superclass::equals))
            return of(classNode);

        return parentTypes.stream()
            .map(type -> buildClassPath(new TypeHierarchyNode<>(type, classNode)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    private Collection<TypeSearchNode<T>> combineClassNodes(@NotNull Collection<TypeHierarchyNode<T>> classNodes) {
        if (classNodes.isEmpty())
            return null;

        Map<Class<?>, List<TypeHierarchyNode<T>>> collect = classNodes.stream()
            .collect(groupingBy(it -> it.type, toList()));

        return collect.keySet().stream()
            .map(type -> {
                List<TypeHierarchyNode<T>> classNodeList = collect.get(type);

                @SuppressWarnings("unchecked")
                PropertiesDescriptor<? extends T> descriptor = classNodeList.stream()
                    .map(classNode -> (PropertiesDescriptor)classNode.descriptor)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseGet(() -> this.propertiesExtractor.getPropertiesDescriptor(type));

                Collection<TypeHierarchyNode<T>> forwardNodes = classNodeList.stream()
                    .map(classNode -> classNode.child)
                    .filter(Objects::nonNull)
                    .collect(toList());

                return new TypeSearchNode<>(descriptor, combineClassNodes(forwardNodes));
            })
            .collect(toList());
    }
}
