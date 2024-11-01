module dev.ikm.commons.demo {
    requires dev.ikm.plugins.api;
    requires dev.ikm.plugins.identicon.api;
    requires dev.ikm.plugin.service.loader;
    requires dev.ikm.tinkar.common;
    requires dev.ikm.tinkar.provider.executor;
    requires dev.ikm.tinkar.plugin.service.boot;

    exports dev.ikm.commons.demo;

    uses dev.ikm.plugins.api.Plugin;
    uses dev.ikm.plugins.identicon.api.IdenticonPlugin;
    uses dev.ikm.tinkar.common.service.PluginServiceLoader;
}