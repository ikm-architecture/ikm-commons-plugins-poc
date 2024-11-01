module dev.ikm.plugins.identicon.lifehash {
    requires dev.ikm.plugins.identicon.api;
    requires java.desktop;
    requires com.sparrowwallet.toucan;

    exports dev.ikm.plugins.identicon.lifehash;

    uses dev.ikm.plugins.identicon.api.IdenticonPlugin;

    provides dev.ikm.plugins.identicon.api.IdenticonPlugin
            with dev.ikm.plugins.identicon.lifehash.LifeHashIdenticonPlugin;
}