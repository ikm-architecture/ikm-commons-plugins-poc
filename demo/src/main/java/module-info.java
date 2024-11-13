module dev.ikm.commons.demo {
    requires dev.ikm.plugins.identicon.api;
    requires dev.ikm.tinkar.common;
    requires dev.ikm.tinkar.plugin.service.boot;

    exports dev.ikm.commons.demo;

    uses dev.ikm.plugins.identicon.api.IdenticonPlugin;
    uses dev.ikm.tinkar.common.service.PluginServiceLoader;
}