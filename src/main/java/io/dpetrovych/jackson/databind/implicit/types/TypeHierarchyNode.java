package io.dpetrovych.jackson.databind.implicit.types;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class TypeHierarchyNode<T> {
    final Class<?> type;
    final SubTypeDescriptor<? extends T> descriptor;
    final TypeHierarchyNode<T> child;

    public TypeHierarchyNode(@NotNull SubTypeDescriptor<? extends T> descriptor) {
        this.type = descriptor.type;
        this.descriptor = descriptor;
        this.child = null;
    }

    public TypeHierarchyNode(@NotNull Class<?> cls, @NotNull TypeHierarchyNode<T> child) {
        this.type = cls;
        this.child = child;
        this.descriptor = null;
    }

    public Collection<Class<?>> getParentTypes(boolean includeInterfaces) {
        List<Class<?>> parentTypes = new ArrayList<>();

        Class<?> superType = type.getSuperclass();
        if (superType != null)
            parentTypes.add(superType);

        if (includeInterfaces)
            Collections.addAll(parentTypes, this.type.getInterfaces());

        return parentTypes;
    }
}
