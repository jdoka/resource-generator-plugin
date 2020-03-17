package ru.dvd.devops.jndi.maven.plugin

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import ru.dvd.devops.jndi.core.JndiResourcesProcessor

//todo: реализовать maven plugin
@Mojo(name = "generateResources", defaultPhase = LifecyclePhase.PROCESS_CLASSES, threadSafe = true)
class JndiResourcesPlugin extends AbstractMojo {

    static final String EXTENSION_NAME = 'jndiResources'

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    void execute() throws MojoExecutionException, MojoFailureException {
        def processor = new JndiResourcesProcessor(project.getBasedir(), getServerName())
    }

    private String getServerName() {
        def plugin = project.getPlugin(EXTENSION_NAME)
        def extensions = plugin.getExtensions()
        ""
    }
}
