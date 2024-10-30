module dev.ikm.plugins.editing.api {
    requires java.desktop;
    requires dev.ikm.plugins.api;

    exports dev.ikm.plugins.editing.api;

    uses dev.ikm.plugins.editing.api.EditingPlugin;
    provides dev.ikm.plugins.editing.api.EditingPlugin
            with dev.ikm.plugins.editing.api.DefaultEditingPlugin;
}