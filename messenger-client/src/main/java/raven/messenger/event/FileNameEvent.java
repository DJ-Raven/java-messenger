package raven.messenger.event;

import java.util.function.Consumer;

public abstract class FileNameEvent {

    private Consumer<Object> eventFileNameChange;

    public abstract void setName(String name);

    public void setEventFileNameChanged(Consumer<Object> event) {
        this.eventFileNameChange = event;
    }

    protected void runEvent(String name) {
        if (eventFileNameChange != null) {
            eventFileNameChange.accept(name);
        }
    }
}
