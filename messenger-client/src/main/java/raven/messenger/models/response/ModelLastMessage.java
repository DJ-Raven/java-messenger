package raven.messenger.models.response;

import org.json.JSONObject;
import raven.messenger.manager.ProfileManager;
import raven.messenger.models.file.FileType;
import raven.messenger.socket.MessageType;
import raven.messenger.socket.models.ModelSendMessage;

public class ModelLastMessage {

    public boolean isYou() {
        return you;
    }

    public void setYou(boolean you) {
        this.you = you;
    }

    public int getFromUser() {
        return fromUser;
    }

    public void setFromUser(int fromUser) {
        this.fromUser = fromUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public ModelLastMessage(FileType fileType) {
        you = true;
        fromUser = ProfileManager.getInstance().getProfile().getUserId();
        messageType = MessageType.toMessageType(fileType.toString());
    }

    public ModelLastMessage(ModelSendMessage ms) {
        you = true;
        fromUser = ProfileManager.getInstance().getProfile().getUserId();
        message = ms.getMessage();
        messageType = ms.getMessageType();
    }

    public ModelLastMessage(ModelMessage ms) {
        you = false;
        fromUser = ms.getFromUser();
        message = ms.getMessage();
        messageType = ms.getType();
    }

    public ModelLastMessage(JSONObject json) {
        you = json.getBoolean("you");
        fromUser = json.getInt("from_user");
        message = json.getString("message");
        messageType = MessageType.toMessageType(json.getString("type"));
    }

    private ModelLastMessage(boolean you, int fromUser, String message, MessageType messageType) {
        this.you = you;
        this.fromUser = fromUser;
        this.message = message;
        this.messageType = messageType;
    }

    private boolean you;
    private int fromUser;
    private String message;
    private MessageType messageType;

    @Override
    public String toString() {
        return message;
    }

    public static ModelLastMessage createAsJoined() {
        return new ModelLastMessage(true, 0, "joined", MessageType.TEXT);
    }
}
