package ru.dvd.devops.jndi.generator.jetty.thymeleaf

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

class GenerationService {
    private static final Logger logger = LoggerFactory.getLogger(GenerationService.class)
    private static final String DEFAULT_TEMPLATE_MODE = "XML"
    private static final String TEMPLATE_SUFFIX = ".thymes"
    private static final String TEMPLATE_PATH = "templates/"

    private TemplateEngine templateEngine

    GenerationService() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver()
        templateResolver.setTemplateMode(DEFAULT_TEMPLATE_MODE)
        templateResolver.setSuffix(TEMPLATE_SUFFIX)
        templateResolver.setPrefix(TEMPLATE_PATH)
        templateEngine = new TemplateEngine()
        this.templateEngine.setTemplateResolver(templateResolver)
    }

    void generate(String templateName, Object context, Writer output) {
        Context thymeleafContext = new Context()
        logger.debug("About to call generationService.process")
        thymeleafContext.setVariable("context", context)
        templateEngine.process(templateName, thymeleafContext, output)
    }
}
