package raven.messenger.socket.models;

import org.json.JSONObject;
import raven.messenger.manager.ProfileManager;
import raven.messenger.models.other.JsonModel;
import raven.messenger.socket.ChatType;
import raven.messenger.socket.MessageType;

public class ModelSendMessage implements JsonModel {

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelSendMessage(ChatType chatType, int target, MessageType messageType, String message) {
        this.chatType = chatType;
        this.target = target;
        this.messageType = messageType;
        this.message = message;
    }

    public ModelSendMessage(ChatType chatType, int target, MessageType messageType, String message, int referenceId) {
        this.chatType = chatType;
        this.target = target;
        this.messageType = messageType;
        this.message = message;
        this.referenceId = referenceId;
    }

    private ChatType chatType;
    private int target;
    private MessageType messageType;
    private String message;
    private int referenceId;

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("type", chatType.toString());
        json.put("from_name", ProfileManager.getInstance().getProfile().getName().toJsonObject());
        json.put("target", target);
        json.put("message_type", messageType.toString());
        json.put("message", message);
        if (referenceId > 0) {
            json.put("reference_id", referenceId);
        }
        return json;
    }
}
