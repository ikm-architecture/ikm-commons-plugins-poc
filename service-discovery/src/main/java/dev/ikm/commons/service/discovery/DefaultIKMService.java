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
package dev.ikm.commons.service.discovery;

import dev.ikm.commons.service.loader.IKMService;
import dev.ikm.commons.service.loader.IKMServiceLoader;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * IKMServiceLoader is a utility class used to load service providers using the {@link ServiceLoader} mechanism.
 */
public class DefaultIKMService implements IKMService {
    private final AtomicReference<IKMServiceLoader> serviceLoader = new AtomicReference<>();
    private final ConcurrentHashMap<Class, Object> cachedServices = new ConcurrentHashMap();
    private static IKMService instance;

    private DefaultIKMService() {}

    public static synchronized IKMService get() {
        if (instance == null) {
            instance = new DefaultIKMService();
        }
        return instance;
    }

    @Override
    public <S> ServiceLoader<S> loader(Class<S> service) {
        if (serviceLoader.get() == null) {
            if (!DefaultIKMService.class.getModule().canUse(service)) {
                DefaultIKMService.class.getModule().addUses(service);
            }
            return ServiceLoader.load(service);
        }
        try {
            return serviceLoader.get().loader(service);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setIKMServiceLoader(IKMServiceLoader ikmServiceLoader) {
        serviceLoader.set(ikmServiceLoader);
    }

    @Override
    public <S> S first(Class<S> service) {
        return (S) cachedServices.computeIfAbsent(service, serviceClass -> {
            ServiceLoader<?> loader = this.loader(service);
            if (loader.stream().count() > 1) {
                throw new IllegalStateException("More than one service loaded for: " + service);
            }
            return loader.findFirst().get();
        });
     }

    @Override
    public Class<?> forName(String className) throws ClassNotFoundException {
     if (serviceLoader.get() == null) {
         return Class.forName(className);
     }
     return serviceLoader.get().forName(className);
    }

}
