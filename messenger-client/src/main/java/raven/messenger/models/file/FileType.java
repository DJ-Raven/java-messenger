package raven.messenger.models.file;

public enum FileType {

    VOICE("v"), FILE("f"), PHOTO("p");

    private String value;

    private FileType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static FileType toFileType(String type) {
        if (type.equals("v")) {
            return VOICE;
        } else if (type.equals("p")) {
            return PHOTO;
        } else {
            return FILE;
        }
    }
}
