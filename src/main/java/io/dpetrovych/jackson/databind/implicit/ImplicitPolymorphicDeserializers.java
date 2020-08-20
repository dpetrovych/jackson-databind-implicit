package io.dpetrovych.jackson.databind.implicit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.Deserializers;

import java.util.Arrays;

public class ImplicitPolymorphicDeserializers extends Deserializers.Base {

    public JsonDeserializer<?> findBeanDeserializer(
            JavaType javaType,
            DeserializationConfig deserializationConfig,
            BeanDescription beanDescription) throws JsonMappingException {

        Class<?> valueType = javaType.getRawClass();

        if (!hasDeserializerFor(deserializationConfig, valueType))
            return null;

        JsonSubTypes subTypesAnnotation = valueType.getAnnotation(JsonSubTypes.class);
        if (subTypesAnnotation == null)
            throw new JsonMappingException(null, ErrorMessages.subtypesAnnotationRequired(valueType));

        Class<?>[] subTypes = Arrays.stream(subTypesAnnotation.value())
            .map(JsonSubTypes.Type::value)
            .toArray(Class<?>[]::new);
        
        return new ImplicitPolymorphicDeserializer<>(subTypes, javaType, valueType);
    }

    @Override
    public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
        JsonImplicitTypes implicitTypesAnnotation = valueType.getAnnotation(JsonImplicitTypes.class);
        return implicitTypesAnnotation != null;
    }
}
