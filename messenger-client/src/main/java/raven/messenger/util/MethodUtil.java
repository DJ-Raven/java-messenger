package raven.messenger.util;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.coobird.thumbnailator.Thumbnails;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MethodUtil {

    public static int checkPasswordStrength(String password) {
        int score = 0;
        if (password.length() >= 8) {
            score++;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        if (hasUppercase) {
            score++;
        }
        boolean hasLowercase = !password.equals(password.toUpperCase());
        if (hasLowercase) {
            score++;
        }
        boolean hasDigit = password.matches(".*\\d.*");
        if (hasDigit) {
            score++;
        }
        boolean hasSpecialChar = !password.matches("[A-Za-z0-9]*");
        if (hasSpecialChar) {
            score++;
        }
        if (score < 3) {
            return 1;
        } else if (score < 5) {
            return 2;
        } else {
            return 3;
        }
    }

    public static String convertMillisToTime(long millis) {
        long minutes = (millis / (1000 * 60)) % 60;
        long seconds = (millis / 1000) % 60;
        long milliseconds = (millis % 1000) / 100;
        String timeString = String.format("%02d:%02d:%01d", minutes, seconds, milliseconds);
        return timeString;
    }

    public static float easingMilliseconds(long milliseconds) {
        final float MAX_MILLISECONDS = 1000;
        float remainder = milliseconds % MAX_MILLISECONDS;
        float easingValue;
        if (remainder <= MAX_MILLISECONDS / 2f) {
            easingValue = remainder / (MAX_MILLISECONDS / 2f);
        } else {
            easingValue = 1f - (remainder / (MAX_MILLISECONDS / 2f)) + 1f;
        }
        return easingValue;
    }

    public static String convertSecondsToTime(double seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int remainingSeconds = (int) (seconds % 60);
        String formattedTime;
        if (hours > 0) {
            formattedTime = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        } else {
            formattedTime = String.format("%02d:%02d", minutes, remainingSeconds);
        }
        return formattedTime;
    }

    public static String formatSize(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static boolean isImageFile(String filePath) {
        String extension = getFileExtension(filePath);
        if (extension != null && extension.matches("(?i)(png|jpe?g|gif|bmp)$")) {
            return true;
        }
        return false;
    }

    private static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
            return filePath.substring(dotIndex + 1).toLowerCase();
        }
        return null;
    }

    public static Date stringToDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Icon createIcon(String path, float scale) {
        return createIcon(path, scale, null);

    }

    public static Icon createIcon(String path, float scale, Color color) {
        return createIcon(path, scale, color, null);
    }

    public static Icon createIcon(String path, float scale, Color light, Color dark) {
        FlatSVGIcon icon = new FlatSVGIcon(path, scale);
        if (light != null) {
            FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter();
            if (dark == null) {
                colorFilter.add(Color.decode("#969696"), light);
            } else {
                colorFilter.add(Color.decode("#969696"), light, dark);
            }
            icon.setColorFilter(colorFilter);
        }
        return icon;
    }

    public static String getProfileString(String name) {
        int indexSpace = name.indexOf(" ");
        if (indexSpace == -1) {
            return name.substring(0, Math.min(2, name.length())).toUpperCase();
        } else {
            return (name.charAt(0) + "" + name.charAt(indexSpace + 1)).toUpperCase();
        }
    }

    public static File compressImage(File file) throws IOException {
        Path tempPath = Files.createTempFile("temp_", "_photo.jpg");
        File output = tempPath.toFile();
        output.deleteOnExit();
        Thumbnails.of(file)
                .addFilter(new ThumbnailsFilterMax(1000, 1000))
                .scale(1f)
                .outputQuality(0.8)
                .toFile(output);
        return output;
    }

    public static void runWithThread(Runnable runnable) {
        new Thread(runnable).start();
    }
}
