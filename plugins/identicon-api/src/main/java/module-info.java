module dev.ikm.plugins.identicon.api {
    uses dev.ikm.plugins.identicon.api.IdenticonPlugin;
    requires dev.ikm.plugins.api;
    requires java.desktop;

    exports dev.ikm.plugins.identicon.api;

    provides dev.ikm.plugins.identicon.api.IdenticonPlugin
            with dev.ikm.plugins.identicon.api.DefaultIdenticonPlugin;
}