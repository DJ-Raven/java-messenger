package raven.messenger.component.chat.model;

import raven.messenger.event.FileNameEvent;
import raven.messenger.models.file.ModelFile;

import java.io.File;

public class ChatFileData extends FileNameEvent {

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        runEvent(name);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public ChatFileData(ModelFile file) {
        this.originalName = file.getOriginalName();
        this.name = file.getName();
        this.size = file.getSize();
    }

    public ChatFileData(File file) {
        this.originalName = file.getName();
        this.size = file.length();
        this.file = file;
    }


    private String originalName;
    private String name;
    private File file;
    private long size;
}
