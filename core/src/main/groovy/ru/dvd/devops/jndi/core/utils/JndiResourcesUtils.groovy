package ru.dvd.devops.jndi.core.utils

import org.apache.commons.collections4.CollectionUtils
import ru.dvd.devops.jndi.core.vo.JndiResources

class JndiResourcesUtils {

    /**
     * @return JndiResources как разницу соответствующих ресурсов {@code <i>  a</i> - <i>b</i>}.
     */
    static JndiResources subtractJndiResources(JndiResources a, JndiResources b) {
        def diff = new JndiResources()
        diff.connectionFactories.addAll(CollectionUtils.subtract(a.connectionFactories, b.connectionFactories))
        diff.queues.addAll(CollectionUtils.subtract(a.queues, b.queues))
        diff.executors.addAll(CollectionUtils.subtract(a.executors, b.executors))
        diff.dataSources.addAll(CollectionUtils.subtract(a.dataSources, b.dataSources))
        diff
    }
}
