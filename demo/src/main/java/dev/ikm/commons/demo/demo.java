package dev.ikm.commons.demo;

import dev.ikm.plugins.identicon.api.IdenticonPlugin;

import java.util.ServiceLoader;

public class demo {

    public static void main(String[] args) {
        System.out.println("Starting...");

        ServiceLoader<IdenticonPlugin> identiconPlugins = ServiceLoader.load(IdenticonPlugin.class);
        System.out.println("Found " + identiconPlugins.stream().count() + " Identicon Plugins:");

        identiconPlugins.stream()
                .map(ServiceLoader.Provider::get)
                .forEach(javaFxIdenticonPlugin -> System.out.println(javaFxIdenticonPlugin.getName()));
    }

}
