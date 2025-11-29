package raven.messenger.plugin.sound.player;

import java.util.EventObject;

public class PlayerEvent extends EventObject {

    private final int currentInSeconds;
    private final float currentInPercent;

    public PlayerEvent(Object source, int currentInSeconds, float currentInPercent) {
        super(source);
        this.currentInSeconds = currentInSeconds;
        this.currentInPercent = currentInPercent;
    }

    public int getCurrentInSeconds() {
        return currentInSeconds;
    }

    public float getCurrentInPercent() {
        return currentInPercent;
    }
}