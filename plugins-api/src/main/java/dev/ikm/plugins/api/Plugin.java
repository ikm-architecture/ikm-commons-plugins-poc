package dev.ikm.plugins.api;

import java.util.UUID;

public interface Plugin {

    UUID getId();

    default String getName() {
        return this.getClass().getName();
    };

}
