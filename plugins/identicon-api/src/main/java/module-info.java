module dev.ikm.plugins.identicon.api {
    requires java.desktop;
    requires dev.ikm.plugins.api;

    exports dev.ikm.plugins.identicon.api;

    uses dev.ikm.plugins.identicon.api.IdenticonPlugin;
    provides dev.ikm.plugins.identicon.api.IdenticonPlugin
            with dev.ikm.plugins.identicon.api.DefaultIdenticonPlugin;
}