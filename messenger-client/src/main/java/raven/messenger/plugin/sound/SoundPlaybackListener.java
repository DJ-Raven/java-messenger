package raven.messenger.plugin.sound;

public interface SoundPlaybackListener {

    void positionChanged(long position, long length);

    void start();

    void stop();
}