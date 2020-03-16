package ru.dvd.devops.jndi.core.spi.vcs

/**
 * SPI-интерфейс для работы с проектом, управляемым системой контроля версий.
 * Реализации должны следовать соглашению SPI {@link java.util.ServiceLoader}.
 */
interface VcsOperations<T> {

    /**
     * Пытается сконфигурировать {@code this} для работы с проектом, управляемым данной системой контроля версий.
     * @param rootDir корневая директория проекта.
     * @return true, если получилось сконфигурировать объект, иначе - false, например, если проект не находится под управлением данной vcs.
     */
    boolean tryConfigure(File rootDir)

    /**
     *
     * Выполняет {@code action} на последнем теггированном коммите текущей ветки.
     * После выполения переключается обратно на последний коммит текущей ветки.
     * @return результат выполения {@code action}. {@code defaultValue}, если результатом выполнения является null.
     */
    T doInLastTaggedCommit(Closure<T> action, T defaultValue)

    /**
     *
     * Выполняет {@code action} на коммите с тегом {@code tag}.
     * После выполения переключается обратно на последний коммит текущей ветки.
     * @return результат выполения {@code action}. {@code defaultValue}, если результатом выполнения является null.
     */
    T doInTaggedCommit(Closure<T> action, T defaultValue, String tag)

}