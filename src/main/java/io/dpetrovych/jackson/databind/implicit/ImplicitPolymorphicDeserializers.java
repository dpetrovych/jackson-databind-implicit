package io.dpetrovych.jackson.databind.implicit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class ImplicitPolymorphicDeserializers extends Deserializers.Base {

    public JsonDeserializer<?> findBeanDeserializer(
            JavaType javaType,
            DeserializationConfig deserializationConfig,
            BeanDescription beanDescription) throws JsonMappingException {

        Class<?> rawClass = javaType.getRawClass();

        JsonImplicitTypes implicitTypesAnnotation = rawClass.getAnnotation(JsonImplicitTypes.class);
        if (implicitTypesAnnotation == null)
            return null;

        JsonSubTypes subTypesAnnotation = rawClass.getAnnotation(JsonSubTypes.class);
        if (subTypesAnnotation == null)
            throw new JsonMappingException(null,
                    String.format("@%s annotation require @%s for type: %s",
                            JsonImplicitTypes.class.getSimpleName(),
                            JsonSubTypes.class.getSimpleName(),
                            rawClass.getName()));

        Collection<BeanDescription> typeDescriptions = Arrays.stream(subTypesAnnotation.value())
                .map(type -> toBeanDescription(deserializationConfig, type.value()))
                .collect(Collectors.toList());

        return new ImplicitPolymorphicDeserializer<>(typeDescriptions, javaType, rawClass);
    }

    private BeanDescription toBeanDescription(DeserializationConfig config, Class<?> clz) {
        JavaType javaType = config.getTypeFactory().constructType(clz);
        return config.introspect(javaType);
    }
}
