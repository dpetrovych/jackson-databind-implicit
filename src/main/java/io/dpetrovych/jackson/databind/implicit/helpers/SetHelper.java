package io.dpetrovych.jackson.databind.implicit.helpers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SetHelper {
    @NotNull
    public static <T> Set<T> fromIterable(Iterator<T> iterable) {
        Set<T> set = new HashSet<T>();
        iterable.forEachRemaining(set::add);
        return set;
    }

    @NotNull
    @Contract(pure = true)
    public static <T> Set<T> intersect(@NotNull Collection<T> first, @NotNull Collection<T> second) {
        Set<T> set = new HashSet<>(first);
        set.retainAll(second);
        return set;
    }

    @Contract(pure = true)
    public static <T> boolean hasIntersection(@NotNull Set<T> first, @NotNull Set<T> second) {
        if (first.size() > second.size()) {
            Set<T> temp = first;
            first = second;
            second = temp;
        }

        for (T val : first) {
            if (second.contains(val))
                return true;
        }

        return false;
    }

    @NotNull
    @Contract(pure = true)
    public static <T> Set<T> subtract(@NotNull Collection<T> first, @NotNull Collection<T> second) {
        Set<T> set = new HashSet<>(first);
        set.removeAll(second);
        return set;
    }
}
