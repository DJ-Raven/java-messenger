package raven.messenger.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.ImageFilter;

import java.awt.image.BufferedImage;

public class ThumbnailsFilterMax implements ImageFilter {

    private final int maxWidth;
    private final int maxHeight;

    public ThumbnailsFilterMax(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public BufferedImage apply(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        if (width <= maxWidth && height <= maxHeight) {
            return bufferedImage;
        }
        try {
            return Thumbnails.of(bufferedImage).size(maxWidth, maxHeight).asBufferedImage();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
