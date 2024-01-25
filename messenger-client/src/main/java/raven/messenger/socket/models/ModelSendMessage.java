package raven.messenger.socket.models;

import org.json.JSONObject;
import raven.messenger.models.other.JsonModel;
import raven.messenger.socket.MessageType;

public class ModelSendMessage implements JsonModel {

    public int getToUser() {
        return toUser;
    }

    public void setToUser(int toUser) {
        this.toUser = toUser;
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

    public ModelSendMessage(int toUser, MessageType messageType, String message) {
        this.toUser = toUser;
        this.messageType = messageType;
        this.message = message;
    }

    public ModelSendMessage(int toUser, MessageType messageType, String message, int referenceId) {
        this.toUser = toUser;
        this.messageType = messageType;
        this.message = message;
        this.referenceId = referenceId;
    }

    private int toUser;
    private MessageType messageType;
    private String message;
    private int referenceId;

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("to_user", toUser);
        json.put("message_type", messageType.toString());
        json.put("message", message);
        if (referenceId > 0) {
            json.put("reference_id", referenceId);
        }
        return json;
    }
}
