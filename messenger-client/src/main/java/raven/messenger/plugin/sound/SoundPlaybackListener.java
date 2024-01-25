package raven.messenger.plugin.sound;

public interface SoundPlaybackListener {

    public void positionChanged(long position, long length);

    public void start();

    public void stop();
}