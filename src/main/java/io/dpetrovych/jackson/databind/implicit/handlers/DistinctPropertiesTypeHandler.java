package io.dpetrovych.jackson.databind.implicit.handlers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dpetrovych.jackson.databind.implicit.helpers.SetHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.fromIterable;
import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.intersect;
import static io.dpetrovych.jackson.databind.implicit.helpers.SetHelper.subtract;


public class DistinctPropertiesTypeHandler<T> implements TypeHandler<T> {
    private final Collection<BeanDescription> typeDescriptions;
    private final JavaType superType;

    public DistinctPropertiesTypeHandler(Collection<BeanDescription> typeDescriptions, JavaType superType) {
        this.typeDescriptions = typeDescriptions;
        this.superType = superType;
    }

    @Override
    public Class<? extends T> getTypeToCast(JsonParser parser, ObjectNode node) throws JsonProcessingException {
        Collection<PropsToClass> distinctPropsToClasses = getDistinctPropertiesToClasses();
        Map<Boolean, List<PropsToClass>> hasDistinctProperties = distinctPropsToClasses.stream()
                .collect(Collectors.partitioningBy(it -> !it.properties.isEmpty()));

        Set<String> nodeFields = fromIterable(node.fieldNames());

        Optional<Class<? extends T>> byDistinct = getTypeFromDistinctProperties(nodeFields, hasDistinctProperties.get(true));
        if (byDistinct.isPresent())
            return byDistinct.get();

        return getTypeFromCommonProperties(parser, hasDistinctProperties.get(false));
    }

    @NotNull
    private Collection<PropsToClass> getDistinctPropertiesToClasses() {
        Collection<PropsToClass> propsToClasses = typeDescriptions
                .stream()
                .map(type -> {
                    Set<String> properties = type.findProperties().stream()
                            .map(BeanPropertyDefinition::getName)
                            .collect(Collectors.toSet());

                    return new PropsToClass(properties, (Class<? extends T>) type.getBeanClass());
                })
                .collect(Collectors.toList());

        Set<String> commonProps = propsToClasses.stream()
                .map(PropsToClass::getProperties)
                .reduce(SetHelper::intersect)
                .orElseGet(HashSet::new);

        return propsToClasses.stream()
                .map(it -> new PropsToClass(subtract(it.getProperties(), commonProps), it.getBeanClass()))
                .collect(Collectors.toList());
    }

    private Optional<Class<? extends T>> getTypeFromDistinctProperties(@NotNull Set<String> nodeFields, @NotNull List<PropsToClass> propsToClasses) {
        return propsToClasses.stream()
                .filter(it -> !intersect(nodeFields, it.getProperties()).isEmpty())
                .findFirst()
                .map(PropsToClass::getBeanClass);
    }

    @NotNull
    private Class<? extends T> getTypeFromCommonProperties(JsonParser parser, @NotNull List<PropsToClass> propsToClasses) throws InvalidDefinitionException {
        if (propsToClasses.size() > 1)
            throw InvalidDefinitionException.from(parser, createTooManyTypesMessage(propsToClasses), superType);

        if (propsToClasses.size() == 0)
            throw InvalidDefinitionException.from(parser, createNoTypesMessage(), superType);

        return propsToClasses.get(0).getBeanClass();
    }

    private String createTooManyTypesMessage(@NotNull List<PropsToClass> propsToClasses) {
        List<String> typeNames = propsToClasses.stream().map(it -> it.getBeanClass().getName()).collect(Collectors.toList());
        return String.format("%s types matches the same object: %s", typeNames.size(), typeNames);
    }

    private String createNoTypesMessage() {
        return "No types matches the object";
    }

    class PropsToClass {
        private final Set<String> properties;
        private final Class<? extends T> beanClass;

        public PropsToClass(Set<String> properties, Class<? extends T> beanClass) {

            this.properties = properties;
            this.beanClass = beanClass;
        }

        public Set<String> getProperties() {
            return properties;
        }

        public Class<? extends T> getBeanClass() {
            return beanClass;
        }

        @Override
        public String toString() {
            return "{properties=" + properties + ", beanClass=" + beanClass + "}";
        }
    }
}
