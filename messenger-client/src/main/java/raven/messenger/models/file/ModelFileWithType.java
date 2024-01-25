package raven.messenger.models.file;

import java.io.File;

public class ModelFileWithType {

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public ModelFileWithType(File file, FileType type) {
        this.file = file;
        this.type = type;
    }

    private File file;
    private FileType type;
}
