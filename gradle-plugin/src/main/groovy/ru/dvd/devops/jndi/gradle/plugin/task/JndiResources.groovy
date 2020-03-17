package ru.dvd.devops.jndi.gradle.plugin.task

import org.gradle.util.ConfigureUtil

/**
 * Ресурсы контейнера
 */
class JndiResources {
    List<JdbcResource> dataSources = []
    List<JmsConnectionResource> connectionFactories = []
    List<JmsQueueResource> queues = []
    List<ExecutorsResource> executors = []

    def dataSource(Closure configureClosure) {
        JdbcResource dataSource = new JdbcResource()
        ConfigureUtil.configure(configureClosure, dataSource)
        dataSources << dataSource
    }

    def connectionFactory(Closure configureClosure) {
        JmsConnectionResource resource = new JmsConnectionResource()
        ConfigureUtil.configure(configureClosure, resource)
        connectionFactories << resource
    }

    def queue(Closure configureClosure) {
        JmsQueueResource resource = new JmsQueueResource()
        ConfigureUtil.configure(configureClosure, resource)
        queues << resource
    }

    def executor(Closure configureClosure) {
        ExecutorsResource resource = new ExecutorsResource()
        ConfigureUtil.configure(configureClosure, resource)
        executors << resource
    }

    @Override
    String toString() {
        return "ResourcesGroup{" +
                "dataSources=" + dataSources +
                ", connectionFactories=" + connectionFactories +
                ", queues=" + queues +
                ", executors=" + executors +
                '}'
    }
}

class JndiResource {
    String name
    String description

    @Override
    String toString() {
        return "JndiResource{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}'
    }
}

class JmsConnectionResource extends JndiResource {
    String url

    @Override
    String toString() {
        return "JmsConnectionResource{" +
                "name='" + name + '\'' +
                "url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}'
    }
}

class JmsQueueResource extends JndiResource {
    String target

    @Override
    String toString() {
        return "JmsQueueResource{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                "target='" + target + '\'' +
                '}'
    }
}

class ExecutorsResource extends JndiResource {
    Integer size = 10

    @Override
    String toString() {
        return "ExecutorsResource{" +
                "name='" + name + '\'' +
                "size='" + size + '\'' +
                ", description='" + description + '\'' +
                '}'
    }
}

class JdbcResource extends JndiResource {
    String username
    String password
    String driver
    String url

    @Override
    String toString() {
        return "JdbcResource{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                '}'
    }
}