package ru.dvd.devops.jndi.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.GradleBuild
import org.gradle.buildinit.tasks.InitBuild
import org.gradle.jvm.tasks.Jar
import org.gradle.language.assembler.tasks.Assemble
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.dvd.devops.jndi.core.JndiResourcesProcessor
import ru.dvd.devops.jndi.gradle.plugin.mapper.TaskConfigMapper
import ru.dvd.devops.jndi.gradle.plugin.task.TaskConfig

@SuppressWarnings("GroovyUnusedDeclaration")
class JndiResourcesPlugin implements Plugin<Project> {

    static final Logger logger = LoggerFactory.getLogger(JndiResourcesPlugin.class)

    static final String EXTENSION_NAME = 'jndiResources'
    static final String TASK_NAME = 'generateJndiResources'

    @Override
    void apply(Project project) {
        setupExtensions(project)
        installTasks(project)
    }

    private static setupExtensions(Project project) {
        project.extensions.add(EXTENSION_NAME, new TaskConfig())
    }

    private static installTasks(Project project) {
        project.tasks.create(TASK_NAME).doLast {
            def resourcesProcessor = new JndiResourcesProcessor(getRootDir(project), getServerName(project))
            resourcesProcessor.processResources {
                refreshProject(project)
                TaskConfig config = project.extensions.getByType(TaskConfig.class)
                logger.info("Config: {}" + config)
                new TaskConfigMapper().mapToResourceConfig(config)
            }
        }
    }

    private static File getRootDir(Project project) {
        return project.rootDir
    }

    private static String getServerName(Project project) {
        TaskConfig config = project.extensions.getByType(TaskConfig.class)
        config.serverName
    }

//    todo: сейчас не работает. нужно научиться рефрешить gradle Project при изменении build файла.
//    если невозможно средствами gradle api, можно поступить следующим образом: создавать программно новый project на основе свежего build файла
//    в крайнем случае можно вручную пропарсить файл и вытащить конфиг(не рекомендуется).
    private static refreshProject(Project project) {
        def container = project.tasks
        def build = container.findByName("init") as InitBuild
        build.setupProjectLayout()
    }
}