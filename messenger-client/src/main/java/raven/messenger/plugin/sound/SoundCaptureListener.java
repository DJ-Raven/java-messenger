package raven.messenger.plugin.sound;

public interface SoundCaptureListener {

    void capturing(long millisecond);

    void start();

    void stop();
}