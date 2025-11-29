package raven.messenger.component.chat;

import raven.messenger.models.file.ModelFileWithType;
import raven.messenger.plugin.sound.CaptureData;

public interface ChatActionListener {

    void onSendTextMessage(String text);

    void onSendFileMessage(ModelFileWithType[] files, String text);

    void onMicrophoneCapture(CaptureData captureData);

    void onJoinGroup();
}
