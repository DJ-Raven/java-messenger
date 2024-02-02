package raven.messenger.models.response;

import org.json.JSONObject;
import raven.messenger.models.other.ModelImage;
import raven.messenger.socket.ChatType;
import raven.messenger.util.MethodUtil;

public class ModelChatListItem {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public ModelImage getProfile() {
        return profile;
    }

    public void setProfile(ModelImage profile) {
        this.profile = profile;
    }

    public ModelLastMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ModelLastMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ModelChatListItem(JSONObject json) {
        id = json.getInt("id");
        uuid = json.getString("uuid");
        name = json.getString("name");
        chatType = ChatType.toChatType(json.getString("type"));
        activeStatus = json.getBoolean("active");
        if (!json.isNull("profile")) {
            profile = new ModelImage(json.getJSONObject("profile"));
        }
        if (json.has("last_message")) {
            lastMessage = new ModelLastMessage(json.getJSONObject("last_message"));
        }
    }

    private int id;
    private String uuid;
    private String name;
    private ChatType chatType;
    private boolean activeStatus;
    private ModelImage profile;
    private ModelLastMessage lastMessage;

    public boolean isGroup() {
        return chatType == ChatType.GROUP;
    }

    public String getProfileString() {
        return MethodUtil.getProfileString(name);
    }
}
