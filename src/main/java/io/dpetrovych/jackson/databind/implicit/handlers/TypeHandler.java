package io.dpetrovych.jackson.databind.implicit.handlers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface TypeHandler<T> {
    Class<? extends T> getTypeToCast(JsonParser parser, ObjectNode node) throws JsonProcessingException;
}
