package raven.messenger.socket;

public enum ChatType {

    USER("user"), GROUP("group");

    private final String value;

    private ChatType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ChatType toChatType(String type) {
        if (type.equals("user")) {
            return USER;
        } else if (type.equals("group")) {
            return GROUP;
        }
        return null;
    }
}
