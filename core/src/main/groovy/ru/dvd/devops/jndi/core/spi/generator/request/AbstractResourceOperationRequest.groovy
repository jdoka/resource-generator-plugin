package ru.dvd.devops.jndi.core.spi.generator.request


import ru.dvd.devops.jndi.core.vo.JndiResources

/**
 * Базовый класс для всех операций с jndi-ресурсами
 */
abstract class AbstractResourceOperationRequest {

    AbstractResourceOperationRequest(String outputDir, JndiResources jndiResources) {
        this.outputDir = outputDir
        this.jndiResources = jndiResources
    }
    String outputDir

    JndiResources jndiResources
}
