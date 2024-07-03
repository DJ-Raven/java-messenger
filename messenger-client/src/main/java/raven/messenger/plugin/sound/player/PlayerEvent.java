package raven.messenger.plugin.sound.player;

import java.util.EventObject;

public class PlayerEvent extends EventObject {

    private Mp3Player player;

    public PlayerEvent(Mp3Player source) {
        super(source);
        this.player = source;
    }

    public int getCurrentInSeconds() {
        int seconds = (int) (player.currentFrame / player.frameRatePerMilliseconds + player.position) / 1000;
        return seconds;
    }

    public float getCurrentInPercent() {
        long lengthInMilliseconds = player.getMp3File().getLengthInMilliseconds();
        double current = player.currentFrame / player.frameRatePerMilliseconds / lengthInMilliseconds;
        double value = (double) player.position / lengthInMilliseconds;
        float percent = Math.round((current + value) * 100f) / 100f;
        return percent;
    }
}