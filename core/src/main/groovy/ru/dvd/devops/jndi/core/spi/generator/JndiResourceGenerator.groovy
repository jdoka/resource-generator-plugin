package ru.dvd.devops.jndi.core.spi.generator

import ru.dvd.devops.jndi.core.spi.generator.request.GenerationRequest
import ru.dvd.devops.jndi.core.spi.generator.request.PrintDiffRequest


/**
 * SPI-интерфейс для генерации jndi-ресурсов на сервере.
 * Реализации должны следовать соглашению SPI {@link java.util.ServiceLoader}.
 */
interface JndiResourceGenerator {
    /**
     * @return возвращает коллекцию имен реализаций сервлет-контейнеров, для которых данный генератор может генерировать ресурсы.
     * Не должен возвращать null.
     */
    Collection<String> getAllowedServerNames()

    /**
     * Может ли быть применен данный генератор для генерации ресурсов сервера serverName.
     * @param serverName наименование конкретной реализации сервлет-контейнера, например Jetty, Tomcat и т.д.
     *        Правильное значение см. в документации конкретной реализации генератора.
     */
    default boolean isAllowedGenerator(String serverName) {
        return getAllowedServerNames().contains(serverName)
    }

    /**
     *  Сгенерировать jndi-ресурсы для сервера.
     */
    void generateJndiResources(GenerationRequest request)

    /**
     * Напечатать файл с диффом ресурсов.
     */
    void printJndiResourcesDiff(PrintDiffRequest request)

}