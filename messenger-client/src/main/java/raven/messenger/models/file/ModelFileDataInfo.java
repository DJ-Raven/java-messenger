package raven.messenger.models.file;

public class ModelFileDataInfo implements ModelFileInfo{

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public FileType getType() {
        return FileType.FILE;
    }
}
