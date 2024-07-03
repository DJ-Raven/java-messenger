package raven.messenger.component.chat;

import raven.messenger.component.chat.model.ChatFileData;
import raven.messenger.component.chat.model.ChatPhotoData;
import raven.messenger.component.chat.model.ChatSoundData;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;

public class Myself extends ChatItemOption {

    public String getMessage() {
        return message;
    }

    public ChatSoundData getVoice() {
        return voice;
    }

    public ChatPhotoData getPhoto() {
        return photo;
    }

    public ChatFileData getFile() {
        return file;
    }

    public boolean isSent() {
        return sent;
    }

    public boolean isSeen() {
        return seen;
    }

    private String message;
    private ChatSoundData voice;
    private ChatPhotoData photo;
    private ChatFileData file;
    private boolean sent;
    private boolean seen;

    public String getConstraints() {
        return "width 150::400,gapleft 50,trailing";
    }

    protected Myself(ChatModel chatModel) {
        super(chatModel);
    }

    public Myself setMessage(String message) {
        this.message = message;
        return this;
    }

    public Myself setVoice(ChatSoundData voice) {
        this.voice = voice;
        return this;
    }

    public Myself setPhotoData(ChatPhotoData photo) {
        this.photo = photo;
        return this;
    }

    public Myself setFileData(ChatFileData file) {
        this.file = file;
        return this;
    }

    public Myself setSent(boolean sent) {
        this.sent = sent;
        if (sent) {
            if (componentMap != null) {
                JLabel label = (JLabel) getComponentMap("sent");
                if (getPhoto() != null) {
                    label.setIcon(MethodUtil.createIcon("raven/messenger/icon/tick.svg", 0.7f, Color.decode("#4E4E4E"), Color.decode("#C6C6C6")));
                } else {
                    label.setIcon(MethodUtil.createIcon("raven/messenger/icon/tick.svg", 0.7f, Color.decode("#969696"), Color.decode("#646464")));
                }
            }
        }
        return this;
    }

    public Myself setSeen(boolean seen) {
        this.seen = seen;
        return this;
    }

    public Myself setFileName(String fileName) {
        if (voice != null) {
            voice.setName(fileName);
        } else if (file != null) {
            file.setName(fileName);
        }
        return this;
    }

    @Override
    public Myself build() {
        chatModel.add(this);
        return this;
    }
}
