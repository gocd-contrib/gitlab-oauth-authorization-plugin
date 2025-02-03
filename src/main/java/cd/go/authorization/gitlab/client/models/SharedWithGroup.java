
package cd.go.authorization.gitlab.client.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SharedWithGroup {
    @Expose
    @SerializedName("group_id")
    private long groupId;
    @Expose
    @SerializedName("group_name")
    private String groupName;
    @Expose
    @SerializedName("group_full_path")
    private String groupFullPath;
    @Expose
    @SerializedName("group_access_level")
    private AccessLevel accessLevel;
    @Expose
    @SerializedName("expires_at")
    private String expiresAt;

    SharedWithGroup() {
    }

    public long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }
    
    public String getGroupFullPath() {
        return groupFullPath;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public String getExpiresAt() {
        return expiresAt;
    }
}