package ru.dvd.devops.jndi.core

import org.apache.commons.lang3.StringUtils
import ru.dvd.devops.jndi.core.spi.generator.JndiResourceGenerator
import ru.dvd.devops.jndi.core.spi.generator.request.GenerationRequest
import ru.dvd.devops.jndi.core.spi.generator.request.PrintDiffRequest
import ru.dvd.devops.jndi.core.spi.vcs.VcsOperations
import ru.dvd.devops.jndi.core.utils.JndiResourcesUtils
import ru.dvd.devops.jndi.core.vo.ResourceConfig

class JndiResourcesProcessor {

    private JndiResourceGenerator generator
    private VcsOperations<ResourceConfig> vcsOperations

    JndiResourcesProcessor(File rootDir, String serverName) {
        Objects.requireNonNull(serverName, "Server name must not be null")
        generator = initGenerator(serverName)
        Objects.requireNonNull(generator, "Please, check your classpath. Not found a suitable resource generator implementation for server: " + serverName)
        vcsOperations = initVcs(rootDir)
        Objects.requireNonNull(vcsOperations, "Please, check your classpath. Not found a suitable vcsOperations implementation")
    }

    void processResources(Closure<ResourceConfig> resourceConfigSupplier) {
        def resourceConfig = resourceConfigSupplier.call()
        generator.generateJndiResources(new GenerationRequest(resourceConfig.outputDir, resourceConfig.jndiResources, resourceConfig.params))
        if (resourceConfig.printDiffRequired) {
            def defaultOldResourceConfig = new ResourceConfig()
            def oldResourceConfig = StringUtils.isEmpty(resourceConfig.tagFrom) ?
                    vcsOperations.doInLastTaggedCommit(resourceConfigSupplier, defaultOldResourceConfig) :
                    vcsOperations.doInTaggedCommit(resourceConfigSupplier, defaultOldResourceConfig, resourceConfig.tagFrom)
            generator.printJndiResourcesDiff(createPrintDiffRequest(resourceConfig, oldResourceConfig))
        }
    }

    private static JndiResourceGenerator initGenerator(String serverName) {
        ServiceLoader.load(JndiResourceGenerator.class).find {
            it.isAllowedGenerator(serverName)
        } as JndiResourceGenerator
    }

    private static VcsOperations<ResourceConfig> initVcs(File rootDir) {
        ServiceLoader.load(VcsOperations.class).find {
            it.tryConfigure(rootDir)
        } as VcsOperations<ResourceConfig>
    }

    private PrintDiffRequest createPrintDiffRequest(ResourceConfig currentResourceConfig, ResourceConfig oldResourceConfig) {
        def addedJndiResources = JndiResourcesUtils.subtractJndiResources(currentResourceConfig.jndiResources, oldResourceConfig.jndiResources)
        def removedJndiResources = JndiResourcesUtils.subtractJndiResources(oldResourceConfig.jndiResources, currentResourceConfig.jndiResources)
        new PrintDiffRequest(currentResourceConfig.outputDir, addedJndiResources, removedJndiResources, currentResourceConfig.tagFrom)
    }

}
