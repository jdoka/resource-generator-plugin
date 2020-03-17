**Build Manager Plugin для генерации JNDI ресурсов**

Проект создавался в учебных целях:<br/>

1.Разобраться как устроен программный API различных build manager-ов.<br/>
2.Разобраться как устроен программный API различных VCS.<br/>
3.Научиться работать с механизмом Java SPI.<br/>
4.Научиться настраивать ресурсы на различных контейнерах.<br/>

Плагин полезен для того чтобы хранить все JNDI ресурсы проекта в одном месте.
Это место - билд файл проекта.

**Как использовать gradle-plugin**

Пример ```gradle.build```

```&groovy
buildscript {
    repositories{
        mavenLocal()
        maven {
            url "http://..."
        }
    }
    dependencies {
        classpath "..."
    }
}
repositories {
    mavenLocal()
    maven {
        url "http://..."
    }
}

apply plugin:  'ru.dvd.devops.jndi-resources'

jndiResources {
    outputDir = "."
    printDiffRequired = true
    serverName = 'Jetty'
    jndi {
        dataSource {
            name = "jdbc/db"
            description = 'Доступ к БД'
            username = 'db_user'
            password = 'password'
            url = 'jdbc:oracle:...'
            driver = 'com.oracle.Driver'
        }
        connectionFactory {
            name = "jms/monitoringCF"
            url = "tcp://localhost:61616"
            description = 'Фабрика соединений с брокером очередей мониторинга'
        }
        queue {
            name = "jms/monitoringQueue"
            description = 'Очередь мониторинга'
            target = "Q.MONITORING"
        }
    }
}
```

Теперь запускаем таску
 
 ```./gradlew generateJndiResources```
 
 и в файле ```jetty.xml``` появится раздел конфигурации с перечисленными ресурсами, а в файле ```diff.xml``` 
 появится дифф по ресурсам относительно последнего теггированного коммита.
 
 
 В репозитории имеется плагин для gradle и для maven.<br/>
 Пока поддержка сервера - только Jetty, vcs - только git. Для поддержки других контейнеров(напр. Tomcat) или vcs(напр. svn) 
 нужно подложить соответствующую написанную реализацию в classpath - все заработает автоматически(см. документацию spi).<br/>
 
 **Расширение функционала**
 
 Так, например, если ожидается использовать плагин для проектов svn, то необходимо:<br/>
 1.Написать собственную реализацию интерфейса VcsOperations.<br/>
 2.Упаковать в jar по правилам spi(https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html)<br/>
 3.Поместить данный jar в classpath плагина.<br/>
 Больше ничего делать не нужно - плагин определит, под управлением какой vcs находится проект и соответствующим образом будет с ним работать.
 Если плагин обнаружит что он не умеет работать с текущей vcs, фича printDiffRequired = true будет проигнорирована.
 Аналогично функциональность плагина расширяется и в части поддержки новых серверов - отличие только в том, что автоматически плагин сервер не определит(это на подумать),
 поэтому в конфиге таски прописывается имя сервера.
 
