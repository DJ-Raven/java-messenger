package raven.messenger.plugin.blurhash;

public class Util {

    private static double signPow(double value, double exp) {
        return Math.copySign(Math.pow(Math.abs(value), exp), value);
    }

    static void decodeDC(String str, double[] color) {
        int dcValue = Base83.decode(str);
        color[0] = SRGB.toLinear(dcValue >> 16);
        color[1] = SRGB.toLinear((dcValue >> 8) & 255);
        color[2] = SRGB.toLinear(dcValue & 255);
    }

    static void decodeAC(String str, double realMaxValue, double[] color) {
        int acValue = Base83.decode(str);
        int quantR = acValue / (19 * 19);
        int quantG = (acValue / 19) % 19;
        int quantB = acValue % 19;
        color[0] = signPow((quantR - 9.0) / 9.0, 2.0) * realMaxValue;
        color[1] = signPow((quantG - 9.0) / 9.0, 2.0) * realMaxValue;
        color[2] = signPow((quantB - 9.0) / 9.0, 2.0) * realMaxValue;
    }
}