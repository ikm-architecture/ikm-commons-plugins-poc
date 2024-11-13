package dev.ikm.commons.demo;

import dev.ikm.plugin.layer.IkmServiceManager;
import dev.ikm.plugins.identicon.api.IdenticonPlugin;
import dev.ikm.tinkar.common.service.PluggableService;

import java.nio.file.Path;
import java.util.ServiceLoader;

public class demo {

    public static void main(String[] args) {
        Path lifehashIdenticonPluginPath = Path.of("target/plugins");

        IkmServiceManager.setPluginDirectory(lifehashIdenticonPluginPath);
        ServiceLoader<IdenticonPlugin> identiconPlugins = PluggableService.load(IdenticonPlugin.class);

        System.out.println("Found " + identiconPlugins.stream().count() + " Identicon Plugins:");
        identiconPlugins.stream()
                .map(ServiceLoader.Provider::get)
                .forEach(javaFxIdenticonPlugin -> System.out.println(javaFxIdenticonPlugin.getName()));
    }

}
