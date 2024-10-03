/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ikm.commons.service.loader;

import java.util.ServiceLoader;

/**
 * IKMServiceLoader is a utility class used to load service providers using the {@link ServiceLoader} mechanism.
 */
public interface PluggableService {

    /**
     * Loads an IKMService object for the given service class.
     *
     * @param service the class representing the service to be loaded
     * @param <S>     the type of the service to be loaded
     * @return an IKMService object for the given service class
     * @throws RuntimeException if there is an error accessing the load method or with the reflection
     */
     <S> ServiceLoader<S> load(Class<S> service);

    /**
     * Sets the service loader for the IKMServiceLoader class.
     *
     * @param ikmServiceLoader the service loader object to be set
     * @throws RuntimeException if the load method cannot be accessed or if there is a problem with the reflection
     */
    void setPluggableServiceLoader(PluginServiceLoader ikmServiceLoader);

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

}
