package ru.dvd.devops.jndi.core.vo

class ResourceConfig {
    boolean printDiffRequired
    String tagFrom
    String outputDir
    Map<String, Object> params = new HashMap<>()

    JndiResources jndiResources = new JndiResources()
}
