package dev.ikm.commons.demo;

import dev.ikm.plugin.layer.IkmServiceManager;
import dev.ikm.plugins.identicon.api.IdenticonPlugin;
import dev.ikm.tinkar.common.service.PluggableService;
import dev.ikm.tinkar.common.service.TinkExecutor;

import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

public class demo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting...");

        Path lifehashIdenticonPluginPath = Path.of("/Users/rbradley/Code/ikmdev/ikm-commons-plugins-poc/identicon-lifehash-plugin/target");

        IkmServiceManager.setPluginDirectory(lifehashIdenticonPluginPath);

        TinkExecutor.threadPool().awaitTermination(2, TimeUnit.SECONDS);
        TinkExecutor.threadPool().execute(() -> {
            ServiceLoader<IdenticonPlugin> identiconPlugins = PluggableService.load(IdenticonPlugin.class);

//        ServiceLoader<IdenticonPlugin> identiconPlugins = new IkmPluginServiceLoader().loader(IdenticonPlugin.class);
//        ServiceLoader<IdenticonPlugin> identiconPlugins = pluggableServiceLoaderOptional.get().loader(IdenticonPlugin.class);
//        ServiceLoader<IdenticonPlugin> identiconPlugins = ServiceLoader.load(IdenticonPlugin.class);
            System.out.println("Found " + identiconPlugins.stream().count() + " Identicon Plugins:");

            identiconPlugins.stream()
                    .map(ServiceLoader.Provider::get)
                    .forEach(javaFxIdenticonPlugin -> System.out.println(javaFxIdenticonPlugin.getName()));
        });
        TinkExecutor.threadPool().awaitTermination(2, TimeUnit.SECONDS);
    }

}
