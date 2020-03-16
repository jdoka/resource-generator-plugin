package ru.dvd.devops.jndi.core.vo


class JndiResources {

    List<JmsConnectionFactory> connectionFactories = []
    List<JmsQueue> queues = []
    List<Executors> executors = []
    List<DataSource> dataSources = []

    @Override
    String toString() {
        return "JndiResources{" +
                "connectionFactories='" + connectionFactories + '\'' +
                "queues='" + queues + '\'' +
                "executors='" + executors + '\'' +
                "dataSources='" + dataSources + '\'' +
                '}'
    }
}

class JndiResource {
    String name
    String description

    JndiResource(String name, String description) {
        this.name = name
        this.description = description
    }

    @Override
    String toString() {
        return "JndiResource{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}'
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JndiResource that = (JndiResource) o

        if (description != that.description) return false
        if (name != that.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        return result
    }
}

class JmsConnectionFactory extends JndiResource {
    String url

    JmsConnectionFactory(String name, String description, String url) {
        super(name, description)
        this.url = url
    }

    @Override
    String toString() {
        return "JmsConnectionResource{" +
                "name='" + name + '\'' +
                "url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}'
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof JmsConnectionFactory)) return false
        if (!super.equals(o)) return false

        JmsConnectionFactory that = (JmsConnectionFactory) o

        if (url != that.url) return false

        return true
    }

    int hashCode() {
        int result = super.hashCode()
        result = 31 * result + (url != null ? url.hashCode() : 0)
        return result
    }
}

class JmsQueue extends JndiResource {
    String target

    JmsQueue(String name, String description, String target) {
        super(name, description)
        this.target = target
    }

    @Override
    String toString() {
        return "JmsQueueResource{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                "target='" + target + '\'' +
                '}'
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof JmsQueue)) return false
        if (!super.equals(o)) return false

        JmsQueue that = (JmsQueue) o

        if (target != that.target) return false

        return true
    }

    int hashCode() {
        int result = super.hashCode()
        result = 31 * result + (target != null ? target.hashCode() : 0)
        return result
    }
}

class Executors extends JndiResource {
    Integer size = 10

    Executors(String name, String description, Integer size) {
        super(name, description)
        this.size = size
    }

    @Override
    String toString() {
        return "ExecutorsResource{" +
                "name='" + name + '\'' +
                "size='" + size + '\'' +
                ", description='" + description + '\'' +
                '}'
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Executors)) return false
        if (!super.equals(o)) return false

        Executors that = (Executors) o

        if (size != that.size) return false

        return true
    }

    int hashCode() {
        int result = super.hashCode()
        result = 31 * result + (size != null ? size.hashCode() : 0)
        return result
    }
}

class DataSource extends JndiResource {
    String username
    String password
    String driver
    String url

    DataSource(String name, String description, String username, String password, String driver, String url) {
        super(name, description)
        this.username = username
        this.password = password
        this.driver = driver
        this.url = url
    }

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

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof DataSource)) return false
        if (!super.equals(o)) return false

        DataSource that = (DataSource) o

        if (driver != that.driver) return false
        if (password != that.password) return false
        if (url != that.url) return false
        if (username != that.username) return false

        return true
    }

    int hashCode() {
        int result = super.hashCode()
        result = 31 * result + (username != null ? username.hashCode() : 0)
        result = 31 * result + (password != null ? password.hashCode() : 0)
        result = 31 * result + (driver != null ? driver.hashCode() : 0)
        result = 31 * result + (url != null ? url.hashCode() : 0)
        return result
    }
}

