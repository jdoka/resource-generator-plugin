package ru.dvd.devops.jndi.core.spi.generator.request


import ru.dvd.devops.jndi.core.vo.JndiResources

/**
 * Запрос на создание файла с ресурсами, добавленными или удаленными начиная от @{code changedFrom}.
 */
class PrintDiffRequest extends AbstractResourceOperationRequest {

    JndiResources removedJndiResources
    String changedFrom

    PrintDiffRequest(String outputDir, JndiResources addedJndiResources, JndiResources removedJndiResources, String changedFrom) {
        super(outputDir, addedJndiResources)
        this.removedJndiResources = removedJndiResources
        this.changedFrom = changedFrom
    }

    @Override
    String toString() {
        return "PrintDiffRequest{" +
                ", outputDir=" + outputDir +
                ", addedJndiResources=" + jndiResources +
                ", removedJndiResources=" + removedJndiResources +
                ", changedFrom=" + changedFrom +
                '}'
    }
}
