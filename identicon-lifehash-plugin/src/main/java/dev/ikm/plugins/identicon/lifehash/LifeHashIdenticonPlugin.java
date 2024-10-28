package dev.ikm.plugins.identicon.lifehash;

import com.sparrowwallet.toucan.LifeHash;
import com.sparrowwallet.toucan.LifeHashVersion;
import dev.ikm.plugins.identicon.api.IdenticonPlugin;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.function.Function;

public class LifeHashIdenticonPlugin implements IdenticonPlugin {
    private final UUID uuid;

    public LifeHashIdenticonPlugin() {
        this.uuid = UUID.randomUUID();
    }

    public LifeHashIdenticonPlugin(String uuidSeed) {
        this.uuid = UUID.nameUUIDFromBytes(uuidSeed.getBytes());
    }

    @Override
    public <S> Image getIdenticonImage(int width, int height, S seed, Function<S, String> seedProcessor) {
        boolean hasAlpha = true;
        String seedString = seedProcessor.apply(seed);
        LifeHash.Image lifeHashImage = LifeHash.makeFromUTF8(seedString, LifeHashVersion.FIDUCIAL, 1, hasAlpha);
        BufferedImage bufferedImage = LifeHash.getBufferedImage(lifeHashImage);
        return bufferedImage;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }
}
