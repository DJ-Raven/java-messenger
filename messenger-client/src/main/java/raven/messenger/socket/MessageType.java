package raven.messenger.socket;

public enum MessageType {

    TEXT("t"), VOICE("v"), FILE("f"), PHOTO("p");

    private final String value;

    private MessageType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static MessageType toMessageType(String type) {
        if (type.equals("f")) {
            return FILE;
        } else if (type.equals("v")) {
            return VOICE;
        } else if (type.equals("p")) {
            return PHOTO;
        } else {
            return TEXT;
        }
    }
}
