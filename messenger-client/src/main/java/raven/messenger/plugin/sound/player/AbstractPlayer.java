package raven.messenger.plugin.sound.player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlayer {

    private List<PlayerListener> listeners;

    public void addPlayerListener(PlayerListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removePlayerListener(PlayerListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public void clearPlayerListener() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    protected void fireLengthChanged(PlayerEvent event) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.lengthChanged(event);
            }
        }
    }

    protected void fireStarted(PlayerEvent event) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.started(event);
            }
        }
    }

    protected void fireFinished(PlayerEvent event) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.finished(event);
            }
        }
    }

    protected void firePaused(PlayerEvent event) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.paused(event);
            }
        }
    }
}
