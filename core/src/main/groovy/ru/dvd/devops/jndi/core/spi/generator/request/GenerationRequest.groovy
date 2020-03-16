package ru.dvd.devops.jndi.core.spi.generator.request

import ru.dvd.devops.jndi.core.vo.JndiResources

class GenerationRequest extends AbstractResourceOperationRequest {

    private Map<String, Object> params = new HashMap<>()

    GenerationRequest(String outputDir, JndiResources jndiResources, Map<String, Object> params) {
        super(outputDir, jndiResources)
        this.params = params
    }

    @Override
    String toString() {
        return "GenerationRequest{" +
                ", params=" + params +
                ", outputDir=" + outputDir +
                ", jndiResources=" + jndiResources +
                '}'
    }
}
