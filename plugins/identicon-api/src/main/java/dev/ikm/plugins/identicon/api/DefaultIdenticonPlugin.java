package dev.ikm.plugins.identicon.api;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.function.Function;

public class DefaultIdenticonPlugin implements IdenticonPlugin {
    private final UUID uuid;

    public DefaultIdenticonPlugin() {
        this.uuid = UUID.randomUUID();
    }

    public DefaultIdenticonPlugin(String uuidSeed) {
        this.uuid = UUID.nameUUIDFromBytes(uuidSeed.getBytes());
    }

    @Override
    public <S> Image getIdenticonImage(int width, int height, S seed, Function<S, String> seedProcessor) {
        String seedString = seedProcessor.apply(seed);
        int seedHash = seedString.hashCode();
        int redHash = (byte) Math.abs((byte) (seedHash >> 24));
        int greenHash = (byte) Math.abs((byte) (seedHash >> 16));
        int blueHash = (byte) Math.abs((byte) (seedHash >> 8));
        byte[] hash2 = new byte[]{(byte) Math.abs((byte) (seedHash >> 24)),
                (byte) Math.abs((byte) (seedHash >> 16)),
                (byte) Math.abs((byte) (seedHash >> 8))};
        redHash = redHash < 0 ? (byte) redHash & 0xff : redHash;
        greenHash = greenHash < 0 ? (byte) greenHash & 0xff : greenHash;
        blueHash = blueHash < 0 ? (byte) blueHash & 0xff : blueHash;

        Color background = new Color(255, 255, 255, 0);
        Color foreground = new Color(redHash, greenHash, blueHash, 255);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x < 3 ? x : 4 - x;
                Color pixelColor;
                if ((hash2[i] >> y & 1) == 1) {
                    pixelColor = foreground;
                } else {
                    pixelColor = background;
                }
                bufferedImage.getRaster().setPixel(x, y,
                        new int[]{pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), pixelColor.getAlpha()});
            }
        }
        return bufferedImage;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

}
