package raven.messenger.models.response;

import org.json.JSONObject;
import raven.messenger.models.other.ModelImage;
import raven.messenger.util.MethodUtil;

import java.util.Date;

public class ModelGroup {

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalMember() {
        return totalMember;
    }

    public void setTotalMember(int totalMember) {
        this.totalMember = totalMember;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public ModelImage getProfile() {
        return profile;
    }

    public void setProfile(ModelImage profile) {
        this.profile = profile;
    }

    public ModelGroup(JSONObject json) {
        groupId = json.getInt("group_id");
        groupUuid = json.getString("group_uuid");
        name = json.getString("name");
        totalMember = json.getInt("total_member");
        description = json.getString("description");
        createBy = json.getInt("create_by");
        createDate = MethodUtil.stringToDate(json.getString("create_date"));
        joinDate = json.isNull("join_date") ? null : MethodUtil.stringToDate(json.getString("join_date"));
        if (!json.isNull("profile")) {
            profile = new ModelImage(json.getJSONObject("profile"));
        }
    }

    private int groupId;
    private String groupUuid;
    private String name;
    private int totalMember;
    private String description;
    private int createBy;
    private Date createDate;
    private Date joinDate;
    private ModelImage profile;

    public boolean isJoin() {
        return joinDate != null;
    }
}
