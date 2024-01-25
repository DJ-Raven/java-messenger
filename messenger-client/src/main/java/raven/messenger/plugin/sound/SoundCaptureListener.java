package raven.messenger.plugin.sound;

public interface SoundCaptureListener {

    public void capturing(long millisecond);

    public void start();

    public void stop();
}