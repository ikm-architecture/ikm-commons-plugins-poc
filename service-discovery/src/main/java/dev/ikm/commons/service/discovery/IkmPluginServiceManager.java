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

import dev.ikm.commons.service.discovery.internal.Layers;
import dev.ikm.commons.service.discovery.internal.PluginWatchDirectory;
import dev.ikm.commons.service.loader.PluggableService;
import dev.ikm.commons.service.loader.PluginServiceLoader;
import dev.ikm.commons.service.loader.PluginServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The IkmServiceManager class enables provision of plugin services via jar files.
 * This manager handles loading module layers from jar files places in a specified plugins
 * directory. It provides methods to load and access the plugins.
 * <p>
 * This class follows the Singleton design pattern to ensure that only one instance of the IkmServiceManager exists.
 * Use the {@link #getInstance()} method to initialize and/or retrieve the Singleton instance.
 * <p>s
 * Use the {@link PluginServiceManager#loader(Class)} method to obtain a {@link ServiceLoader} that can be used to load plugin services
 * of a specific type.
 */
public class IkmPluginServiceManager implements PluginServiceManager {
    private static final Logger LOG = LoggerFactory.getLogger(IkmPluginServiceManager.class);

    private static final AtomicReference<IkmPluginServiceManager> instance = new AtomicReference<>();
    public final String PATH_KEY = "dev.ikm.tinkar.plugin.service.boot.IkmServiceManager.PATH_KEY";
    public final String ARTIFACT_KEY = "dev.ikm.tinkar.plugin.service.boot.IkmServiceManager.ARTIFACT_KEY";
    private final String DefaultIKMServiceLoaderArtifactId = "plugin-service-loader";
    private final AtomicReference<PluginServiceLoader> serviceLoader = new AtomicReference<>();
    private final AtomicReference<PluggableService> service = new AtomicReference<>();
    private Layers layers;

    private final ConcurrentHashMap<Class, Object> cachedServices = new ConcurrentHashMap();

    private IkmPluginServiceManager() {
        serviceLoader.set(new IkmPluginServiceLoader());
        service.set(new IkmPluggableService(serviceLoader.get()));
    }

    public static synchronized IkmPluginServiceManager getInstance() {
        if (instance.get() == null) {
            instance.set(new IkmPluginServiceManager());
        }
        return instance.get();
    }

    @Override
    public void setPluginsDirectory(Path pluginsDirectory) {
        this.layers = new Layers(Set.of(new PluginWatchDirectory("Standard plugins directory", pluginsDirectory)));
        this.deployIKMServiceLoader(this.layers.getModuleLayers());
    }

    private Optional<String> findIKMServiceLoaderJar(File dirPath, String artifactKey) {
        File[] filesList = dirPath.listFiles();
        if (filesList == null) {
            return Optional.empty();
        }
        for(File file : filesList) {
            if(file.isFile()) {
                if (file.getName().endsWith(".jar") && file.getName().startsWith(artifactKey)) {
                    return Optional.of(file.getAbsolutePath());
                }
            } else {
                Optional<String> optionalIKMServiceLoaderJar = findIKMServiceLoaderJar(file,artifactKey);
                if (optionalIKMServiceLoaderJar.isPresent()) {
                    return optionalIKMServiceLoaderJar;
                }
            }
        }
        return Optional.empty();
    }

    public void deployIKMServiceLoader(List<ModuleLayer> parentLayers) {
        if (System.getProperty(PATH_KEY) == null) {
            String artifactKey = System.getProperty(ARTIFACT_KEY, DefaultIKMServiceLoaderArtifactId);

            Path pluginServiceLoaderPath = resolveIKMServiceLoaderPath();

            findIKMServiceLoaderJar(pluginServiceLoaderPath.toFile(),
                    artifactKey).ifPresentOrElse(pluggableServiceLoaderJar -> {
                        System.setProperty(PATH_KEY, pluggableServiceLoaderJar);
                        LOG.info("Found pluggable service loader jar: {}", pluggableServiceLoaderJar);
                    }, () -> {
                        throw new RuntimeException("""
                            No pluggable service loader found.
                            Ensure that PATH_KEY and ARTIFACT_KEY system properties are provided,
                            or that a pluggable service provider .jar file is provided at a discoverable location.
                            """);
                    });
        }
        String pluginServiceLoaderPath = System.getProperty(PATH_KEY);

        ModuleLayer pluginServiceLoaderLayer = Layers.createModuleLayer(parentLayers, List.of(Path.of(pluginServiceLoaderPath)));
        ServiceLoader.load(pluginServiceLoaderLayer, IkmPluginServiceLoader.class).findFirst().ifPresent(this::setIKMServiceLoader);
    }

    /**
     * Provides the path to the plugin service loader directory whether running a local build or as an installed application.
     *
     * @return Path to the plugin service loader directory
     */
    private Path resolveIKMServiceLoaderPath() {
        // Initialize the pluginServiceLoaderPath to the installed application plugin service loader directory
        Path pluginServiceLoaderPath = Path.of("/").resolve("Applications").resolve("Orchestrator.app")
                .resolve("Contents").resolve(DefaultIKMServiceLoaderArtifactId);

        // For local maven builds, use the latest plugin service loader expected to exist at the localIKMServiceLoaderPath.
        Path localIKMServiceLoaderPath = Path.of(System.getProperty("user.dir")).resolve("target").resolve(DefaultIKMServiceLoaderArtifactId);
        if (localIKMServiceLoaderPath.toFile().exists()) {
            pluginServiceLoaderPath = localIKMServiceLoaderPath;
        }
        LOG.info("Plugin Service Loader directory: {}", pluginServiceLoaderPath.toAbsolutePath());
        return pluginServiceLoaderPath;
    }

    @Override
    public void setIKMServiceLoader(PluginServiceLoader ikmServiceLoader) {
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
    public <S> ServiceLoader<S> loader(Class<S> service) {
        if (serviceLoader.get() == null) {
            if (!this.getClass().getModule().canUse(service)) {
                this.getClass().getModule().addUses(service);
            }
            return ServiceLoader.load(service);
        }
        return serviceLoader.get().loader(service);
    }

    @Override
    public Class<?> forName(String className) throws ClassNotFoundException {
        if (serviceLoader.get() == null) {
            Set<ClassLoader> classLoaders = this.getClass().getModule().getLayer().parents().stream()
                    .flatMap(moduleLayer -> moduleLayer.modules().stream())
                    .map(Module::getClassLoader)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (ClassLoader classLoader : classLoaders) {
                try {
                    return Class.forName(className, true, classLoader);
                } catch (ClassNotFoundException e) {
                    // Try again...
                }
            }
            throw new ClassNotFoundException(className);
        }
        return serviceLoader.get().forName(className);
    }

}
