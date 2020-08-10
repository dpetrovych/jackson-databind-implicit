package io.dpetrovych.jackson.databind.implicit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dpetrovych.jackson.databind.implicit.handlers.DistinctPropertiesTypeHandler;
import io.dpetrovych.jackson.databind.implicit.handlers.TypeHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;


public class ImplicitPolymorphicDeserializer<T> extends JsonDeserializer<T> {
    private final Collection<BeanDescription> typeDescriptions;
    private final JavaType superType;
    private final Class<T> superClass;

    public ImplicitPolymorphicDeserializer(Collection<BeanDescription> typeDescriptions, JavaType superType, Class<T> superClass) {
        this.typeDescriptions = typeDescriptions;
        this.superType = superType;
        this.superClass = superClass;
    }

    @Override
    public T deserialize(@NotNull JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectNode node = jsonParser.readValueAsTree();
        if (node == null)
            throw new IOException(String.format("Only object node can be deserialized as %s", superClass.getName()));

        TypeHandler<T> typeHandler = new DistinctPropertiesTypeHandler<>(typeDescriptions, superType);
        Class<? extends T> concreteClass = typeHandler.getTypeToCast(jsonParser, node);
        return jsonParser.getCodec().treeToValue(node, concreteClass);
    }
}
