package ru.dvd.devops.jndi.generator.jetty

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.dvd.devops.jndi.core.spi.generator.JndiResourceGenerator
import ru.dvd.devops.jndi.core.spi.generator.request.AbstractResourceOperationRequest
import ru.dvd.devops.jndi.core.spi.generator.request.GenerationRequest
import ru.dvd.devops.jndi.core.spi.generator.request.PrintDiffRequest
import ru.dvd.devops.jndi.generator.jetty.thymeleaf.GenerationService

class JettyJndiResourceGenerator implements JndiResourceGenerator {

    private static final Logger logger = LoggerFactory.getLogger(JettyJndiResourceGenerator.class)

    private static final String SERVER_NAME = 'Jetty'

    private final GenerationService generationService = new GenerationService()

    @Override
    Collection<String> getAllowedServerNames() {
        return Collections.singletonList(SERVER_NAME)
    }


    @Override
    void generateJndiResources(GenerationRequest request) {
        generate(request, 'jetty.xml', 'jetty')
    }

    @Override
    void printJndiResourcesDiff(PrintDiffRequest request) {
        generate(request, 'diff.xml', 'diff')
    }

    private generate(AbstractResourceOperationRequest request, String fileName, String templateName) {
        File outputDir = request.getOutputDir() as File
        if (!outputDir.exists()) {
            throw new IllegalStateException("Can't find directory: " + outputDir.absolutePath)
        }
        if (!outputDir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + outputDir.absolutePath)
        }
        def resultFile = new File(outputDir, fileName)
        logger.debug("About to create result file: {}", resultFile.absolutePath)
        resultFile.createNewFile()
        logger.debug("Result file created: {}", resultFile.absolutePath)
        Writer writer = new FileWriter(resultFile)
        try {
            logger.debug("About to call generationService.generate")
            generationService.generate(templateName, request, writer)
            logger.debug("File {} generated successfully ", resultFile.absolutePath)
        } finally {
            writer.close()
        }
    }
}
