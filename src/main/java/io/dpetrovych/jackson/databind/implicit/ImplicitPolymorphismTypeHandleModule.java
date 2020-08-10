package io.dpetrovych.jackson.databind.implicit;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class ImplicitPolymorphismTypeHandleModule extends SimpleModule {
    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new ImplicitPolymorphicDeserializers());
    }
}
