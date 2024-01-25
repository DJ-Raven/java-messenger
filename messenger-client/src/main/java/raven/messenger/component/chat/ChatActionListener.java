package raven.messenger.component.chat;

import raven.messenger.models.file.ModelFileWithType;
import raven.messenger.plugin.sound.CaptureData;

import java.io.File;

public interface ChatActionListener {

    public void onSendTextMessage(String text);

    public void onSendFileMessage(ModelFileWithType files[], String text);

    public void onMicrophoneCapture(CaptureData captureData);
}
