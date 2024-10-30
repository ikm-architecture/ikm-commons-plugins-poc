package dev.ikm.plugins.editing.api;

import dev.ikm.plugins.api.Plugin;

public interface EditingPlugin extends Plugin {

    <S> String getGreeting();

}
