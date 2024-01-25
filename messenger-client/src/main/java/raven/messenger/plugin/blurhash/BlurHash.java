package raven.messenger.plugin.blurhash;

import java.awt.image.BufferedImage;

public class BlurHash {

    public static BufferedImage decodeAndDraw(String blurHash, int width, int height, double punch, int bufferedImageType) {
        int[] data = decode(blurHash, width, height, punch);
        BufferedImage image = new BufferedImage(width, height, bufferedImageType);
        image.setRGB(0, 0, width, height, data, 0, width);
        return image;
    }

    public static int[] decode(String blurHash, int width, int height, double punch) {
        int blurHashLength = blurHash.length();
        if (blurHashLength < 6) {
            throw new IllegalArgumentException("BlurHash must be at least 6 characters long.");
        }

        //Decode metadata
        int sizeInfo = Base83.decode(blurHash.substring(0, 1));
        int sizeY = sizeInfo / 9 + 1;
        int sizeX = sizeInfo % 9 + 1;

        //Make sure we at least have the right number of characters
        if (blurHashLength != 4 + 2 * sizeX * sizeY) {
            throw new IllegalArgumentException("Invalid BlurHash length.");
        }
        int quantMaxValue = Base83.decode(blurHash.substring(1, 2));
        double realMaxValue = (quantMaxValue + 1) / 166.0 * punch;

        //Decode DC component
        double[][] colors = new double[sizeX * sizeY][3];
        Util.decodeDC(blurHash.substring(2, 6), colors[0]);

        //Decode AC components
        for (int i = 1; i < sizeX * sizeY; i++) {
            Util.decodeAC(blurHash.substring(4 + i * 2, 6 + i * 2), realMaxValue, colors[i]);
        }
        int[] pixels = new int[width * height];
        int pos = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double r = 0, g = 0, b = 0;
                for (int y = 0; y < sizeY; y++) {
                    for (int x = 0; x < sizeX; x++) {
                        double basic = Math.cos(Math.PI * x * i / width)
                                * Math.cos(Math.PI * y * j / height);
                        double[] color = colors[x + y * sizeX];
                        r += (color[0] * basic);
                        g += (color[1] * basic);
                        b += (color[2] * basic);
                    }
                }

                pixels[pos++] = 255 << 24 | (SRGB.fromLinear(r) & 255) << 16
                        | (SRGB.fromLinear(g) & 255) << 8 | (SRGB.fromLinear(b) & 255);
            }
        }
        return pixels;
    }
}