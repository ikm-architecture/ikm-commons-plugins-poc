package dev.ikm.plugins.identicon.api;

import dev.ikm.plugins.api.Plugin;

import java.util.function.Function;
import java.awt.Image;

public interface IdenticonPlugin extends Plugin {

    <S> Image getIdenticonImage(int width, int height, S seed, Function<S, String> seedProcessor);

    default <S> Image getIdenticonImageSquare(int size, S seed, Function<S, String> seedProcessor) {
        return getIdenticonImage(size, size, seed, seedProcessor);
    }

}
