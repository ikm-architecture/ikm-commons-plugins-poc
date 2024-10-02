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
import dev.ikm.commons.service.loader.IKMService;
import dev.ikm.commons.service.loader.IKMServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The IkmServiceManager class enables provision of plugin services via jar files.
 * This manager handles loading module layers from jar files places in a specified plugins
 * directory. It provides methods to load and access the plugins.
 * <p>
 * This class follows the Singleton design pattern to ensure that only one instance of the IkmServiceManager exists.
 * Use the {@link #setPluginDirectory(Path)} method to initialize the service.
 * <p>s
 * Use the {@link dev.ikm.commons.service.loader.IKMService#loader(Class)} method to obtain a {@link ServiceLoader} that can be used to load plugin services
 * of a specific type.
 */
public class ServiceManager {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceManager.class);

    public static final String PATH_KEY = "dev.ikm.tinkar.plugin.service.boot.IkmServiceManager.PATH_KEY";
    public static final String ARTIFACT_KEY = "dev.ikm.tinkar.plugin.service.boot.IkmServiceManager.ARTIFACT_KEY";
    private static final String DefaultIKMServiceLoaderArtifactId = "plugin-service-loader";
    private final Layers layers;
    private static AtomicReference<ServiceManager> singletonReference = new AtomicReference<>();

    /**
     * Creates an instance of the IkmServiceManager class.
     *
     * @param pluginsDirectories a set of PluginWatchDirectory objects representing the directories where plugins are stored
     * @throws IllegalStateException if IkmServiceManager has already been set up
     */
    private ServiceManager(Set<PluginWatchDirectory> pluginsDirectories) {
        if (!ServiceManager.singletonReference.compareAndSet(null, this)) {
            throw new IllegalStateException("IkmServiceManager must only be set up once.");
        }
        this.layers = new Layers(pluginsDirectories);
        deployIKMServiceLoader(this.layers.getModuleLayers());
    }

    public static Optional<String> findIKMServiceLoaderJar(File dirPath, String artifactKey){
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

    public static void deployIKMServiceLoader(List<ModuleLayer> parentLayers) {
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
        ServiceLoader<IKMServiceLoader> pluggableServiceLoaderLoader = ServiceLoader.load(pluginServiceLoaderLayer, IKMServiceLoader.class);
        pluggableServiceLoaderLoader.findFirst().ifPresent(serviceLoader -> DefaultIKMService.get().setIKMServiceLoader(serviceLoader));
    }


    /**
     * Provides the path to the plugin service loader directory whether running a local build or as an installed application.
     *
     * @return Path to the plugin service loader directory
     */
    private static Path resolveIKMServiceLoaderPath() {
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

    /**
     * Sets the directory where plugins are stored.
     *
     * @param pluginDirectory the path to the directory where plugins are stored
     */
    public static void setPluginDirectory(Path pluginDirectory) {
        new ServiceManager(Set.of(new PluginWatchDirectory("Standard plugins directory", pluginDirectory)));
    }

}
