package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.of;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class TypeSearchTreeBuilder<T> {
    private final Collection<PropertiesDescriptor<? extends T>> descriptors;
    private final Class<T> superclass;

    public TypeSearchTreeBuilder(Collection<PropertiesDescriptor<? extends T>> descriptors, Class<T> superclass) {
        this.descriptors = descriptors;
        this.superclass = superclass;
    }

    public TypeSearchNode<T> build() {
        List<TypeHierarchyNode<T>> classNodes = descriptors.stream().map(this::buildClassPath).collect(Collectors.toList());
        return new TypeSearchNode<>(null, combineClassNodes(classNodes));
    }

    @NotNull
    private TypeHierarchyNode<T> buildClassPath(PropertiesDescriptor<? extends T> descriptor) {
        return buildClassPath(new TypeHierarchyNode<>(descriptor))
            .orElseThrow(() -> new RuntimeException(
                String.format("Class %s should be descendant of %s", descriptor.beanClass.getName(), this.superclass.getName())));
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

        Collection<List<TypeHierarchyNode<T>>> collect = classNodes.stream()
            .collect(groupingBy(it -> it.type, toList()))
            .values();

        return collect.stream()
            .map(classNodeList -> {
                PropertiesDescriptor<? extends T> descriptor = classNodeList.stream()
                    .map(classNode -> classNode.descriptor)
                    .filter(Objects::nonNull)
                    .findFirst().orElse(null);

                Collection<TypeHierarchyNode<T>> forwardNodes = classNodeList.stream()
                    .map(classNode -> classNode.child)
                    .filter(Objects::nonNull)
                    .collect(toList());

                return new TypeSearchNode<>(descriptor, combineClassNodes(forwardNodes));
            })
            .collect(toList());
    }
}
