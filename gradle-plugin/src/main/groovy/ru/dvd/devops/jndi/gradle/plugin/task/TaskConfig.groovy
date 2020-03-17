package ru.dvd.devops.jndi.gradle.plugin.task

import org.gradle.util.ConfigureUtil

class TaskConfig {
    String outputDir
    String serverName
    boolean printDiffRequired
    String tagFrom
    JndiResources jndi = new JndiResources()
    Map<String, Object> params = new HashMap<>()

    def jndi(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, jndi)
    }

    def params(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, params)
    }


    @Override
    String toString() {
        return "TaskConfig{" +
                "outputDir='" + outputDir + '\'' +
                ", serverName='" + serverName + '\'' +
                ", printDiffRequired=" + printDiffRequired +
                ", tagFrom='" + tagFrom + '\'' +
                ", jndi=" + jndi +
                ", params=" + params +
                '}';
    }
}
