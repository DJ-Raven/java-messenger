package raven.messenger.event;

public class GlobalEvent {

    private static GlobalEvent instance;

    public static GlobalEvent getInstance() {
        if (instance == null) {
            instance = new GlobalEvent();
        }
        return instance;
    }

    private GlobalEvent() {
    }

    public GroupCreateEvent groupCreateEvent;


    public GroupCreateEvent getGroupCreateEvent() {
        return groupCreateEvent;
    }

    public void setGroupCreateEvent(GroupCreateEvent groupCreateEvent) {
        this.groupCreateEvent = groupCreateEvent;
    }
}
