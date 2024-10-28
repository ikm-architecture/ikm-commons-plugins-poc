module dev.ikm.commons.demo {
    requires dev.ikm.plugins.api;
    requires dev.ikm.plugins.identicon.api;

    exports dev.ikm.commons.demo;

    uses dev.ikm.plugins.api.Plugin;
    uses dev.ikm.plugins.identicon.api.IdenticonPlugin;
}