package raven.messenger.component.chat;

import raven.messenger.component.chat.model.ChatFileData;
import raven.messenger.component.chat.model.ChatPhotoData;
import raven.messenger.component.chat.model.ChatVoiceData;

import javax.swing.*;

public class Recipient extends ChatItemOption {

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Icon getProfile() {
        return profile;
    }

    public String getMessage() {
        return message;
    }

    public ChatVoiceData getVoice() {
        return voice;
    }

    public ChatPhotoData getPhoto() {
        return photo;
    }

    public ChatFileData getFile() {
        return file;
    }

    public boolean isSeen() {
        return seen;
    }

    private int id;
    private String username;
    private Icon profile;
    private String message;
    private ChatVoiceData voice;
    private ChatPhotoData photo;
    private ChatFileData file;
    private boolean seen;

    public String getConstraints() {
        return "width 150::400,gapright 50";
    }

    protected Recipient(ChatModel chatModel) {
        super(chatModel);
    }

    public Recipient setId(int id) {
        this.id = id;
        return this;
    }

    public Recipient setUsername(String username) {
        this.username = username;
        return this;
    }

    public Recipient setProfile(Icon profile) {
        this.profile = profile;
        return this;
    }

    public Recipient setMessage(String message) {
        this.message = message;
        return this;
    }

    public Recipient setVoice(ChatVoiceData voice) {
        this.voice = voice;
        return this;
    }

    public Recipient setPhotoData(ChatPhotoData photo) {
        this.photo = photo;
        return this;
    }

    public Recipient setFileData(ChatFileData file) {
        this.file = file;
        return this;
    }

    public Recipient setSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    @Override
    public Recipient build() {
        chatModel.add(this);
        return this;
    }
}
