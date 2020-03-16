package ru.dvd.devops.jndi.gradle.plugin.mapper

import ru.dvd.devops.jndi.core.vo.*
import ru.dvd.devops.jndi.gradle.plugin.task.*

import java.util.stream.Collectors

class TaskConfigMapper {

    ResourceConfig mapToResourceConfig(TaskConfig taskConfig) {
        def jndiResources = new JndiResources()
        jndiResources.connectionFactories = mapJmsConnectionFactories(taskConfig.jndi.connectionFactories)
        jndiResources.queues = mapJmsQueues(taskConfig.jndi.queues)
        jndiResources.executors = mapExecutors(taskConfig.jndi.executors)
        jndiResources.dataSources = mapDataSources(taskConfig.jndi.dataSources)
        def resourceConfig = new ResourceConfig()
        resourceConfig.jndiResources = jndiResources
        resourceConfig.printDiffRequired = taskConfig.printDiffRequired
        resourceConfig.tagFrom = taskConfig.tagFrom
        resourceConfig.outputDir = taskConfig.outputDir
        resourceConfig.params = new HashMap<>(taskConfig.params)
        resourceConfig
    }

    private List<JmsConnectionFactory> mapJmsConnectionFactories(List<JmsConnectionResource> resources) {
        mapJndiResources(resources, { r -> new JmsConnectionFactory(r.name, r.description, r.url) })
    }

    private List<JmsQueue> mapJmsQueues(List<JmsQueueResource> resources) {
        mapJndiResources(resources, { r -> new JmsQueue(r.name, r.description, r.target) })
    }

    private List<Executors> mapExecutors(List<ExecutorsResource> resources) {
        mapJndiResources(resources, { r -> new Executors(r.name, r.description, r.size) })
    }

    private List<DataSource> mapDataSources(List<JdbcResource> resources) {
        mapJndiResources(resources, { r -> new DataSource(r.name, r.description, r.username, r.password, r.driver, r.url) })
    }

    private <K extends ru.dvd.devops.jndi.gradle.plugin.task.JndiResource, V extends JndiResource> List<V> mapJndiResources(List<K> resources, Closure<V> mapper) {
        resources.stream()
                .map(mapper)
                .collect(Collectors.toList())
    }
}
