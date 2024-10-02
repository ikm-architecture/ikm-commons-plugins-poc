package dev.ikm.commons.service.loader;

import java.nio.file.Path;

public interface ServiceManager {

    /**
     * Loads an IKMService object for the given service class.
     *
     * @param service the class representing the service to be loaded
     * @param <S>     the type of the service to be loaded
     * @return an IKMService object for the given service class
     * @throws RuntimeException if there is an error accessing the load method or with the reflection
     */
    <S> java.util.ServiceLoader<S> loader(Class<S> service);

    /**
     * Sets the service loader for the IKMServiceLoader class.
     *
     * @param ikmServiceLoader the service loader object to be set
     * @throws RuntimeException if the load method cannot be accessed or if there is a problem with the reflection
     */
    void setIKMServiceLoader(ServiceLoader ikmServiceLoader);

    /**
     * A simplified method to load when there should only be one and only one instance of a service.
     * @param service
     * @return
     * @param <S>
     */
    <S> S first(Class<S> service);

    /**
     * Use the class loader provided by the service loader
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    Class<?> forName(String className) throws ClassNotFoundException;

    /**
     * Sets the directory where plugins are stored.
     *
     * @param pluginsDirectory the path to the directory where plugins are stored
     */
    void setPluginsDirectory(Path pluginsDirectory);



}
