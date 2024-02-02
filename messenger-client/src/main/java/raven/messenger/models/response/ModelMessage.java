package raven.messenger.models.response;

import org.json.JSONObject;
import raven.messenger.models.file.ModelFile;
import raven.messenger.socket.ChatType;
import raven.messenger.socket.MessageType;
import raven.messenger.util.MethodUtil;

import java.util.Date;

public class ModelMessage {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public ModelFile getFile() {
        return file;
    }

    public void setFile(ModelFile file) {
        this.file = file;
    }

    public ModelMessage(JSONObject json) {
        id = json.getInt("id");
        uuid = json.getString("uuid");
        fromUser = json.getInt("from_user");
        message = json.getString("message");
        type = MessageType.toMessageType(json.getString("message_type"));
        chatType = ChatType.toChatType(json.getString("type"));
        createDate = MethodUtil.stringToDate(json.getString("create_date"));
        if (!json.isNull("update_date")) {
            updateDate = MethodUtil.stringToDate(json.getString("update_date"));
        }
        if (type != MessageType.TEXT) {
            file = new ModelFile(json.getJSONObject("file"));
        }
    }

    private int id;
    private String uuid;
    private int fromUser;
    private String message;
    private MessageType type;
    private ChatType chatType;
    private Date createDate;
    private Date updateDate;
    private ModelFile file;
}
