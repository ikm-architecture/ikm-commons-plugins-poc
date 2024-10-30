package dev.ikm.plugins.editing.api;

import java.util.UUID;

public class DefaultEditingPlugin implements EditingPlugin {
    private final UUID uuid;

    public DefaultEditingPlugin() {
        this.uuid = UUID.randomUUID();
    }

    public DefaultEditingPlugin(String uuidSeed) {
        this.uuid = UUID.nameUUIDFromBytes(uuidSeed.getBytes());
    }

    @Override
    public String getGreeting() {
        return "Hello World!";
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

}
