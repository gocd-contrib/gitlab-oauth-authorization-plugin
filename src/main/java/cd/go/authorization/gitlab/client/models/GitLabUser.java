/*
 * Copyright 2022 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.authorization.gitlab.client.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static cd.go.authorization.gitlab.utils.Util.GSON;

public class GitLabUser {
    @Expose
    @SerializedName("id")
    private int id;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("username")
    private String username;
    @Expose
    @SerializedName("state")
    private String state;
    @Expose
    @SerializedName("avatar_url")
    private String avatarUrl;
    @Expose
    @SerializedName("web_url")
    private String webUrl;
    @Expose
    @SerializedName("created_at")
    private String createdAt;
    @Expose
    @SerializedName("location")
    private Object location;
    @Expose
    @SerializedName("website_url")
    private String websiteUrl;
    @Expose
    @SerializedName("organization")
    private Object organization;
    @Expose
    @SerializedName("last_sign_in_at")
    private String lastSignInAt;
    @Expose
    @SerializedName("confirmed_at")
    private String confirmedAt;
    @Expose
    @SerializedName("last_activity_on")
    private Object lastActivityOn;
    @Expose
    @SerializedName("email")
    private String email;
    @Expose
    @SerializedName("projects_limit")
    private Integer projectsLimit;
    @Expose
    @SerializedName("current_sign_in_at")
    private String currentSignInAt;
    @Expose
    @SerializedName("can_create_group")
    private Boolean canCreateGroup;
    @Expose
    @SerializedName("can_create_project")
    private Boolean canCreateProject;
    @Expose
    @SerializedName("two_factor_enabled")
    private Boolean twoFactorEnabled;


    public GitLabUser() {
    }

    public GitLabUser(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getState() {
        return state;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Object getLocation() {
        return location;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public Object getOrganization() {
        return organization;
    }

    public String getLastSignInAt() {
        return lastSignInAt;
    }

    public String getConfirmedAt() {
        return confirmedAt;
    }

    public Object getLastActivityOn() {
        return lastActivityOn;
    }

    public Integer getProjectsLimit() {
        return projectsLimit;
    }

    public String getCurrentSignInAt() {
        return currentSignInAt;
    }

    public Boolean getCanCreateGroup() {
        return canCreateGroup;
    }

    public Boolean getCanCreateProject() {
        return canCreateProject;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public static GitLabUser fromJSON(String json) {
        return GSON.fromJson(json, GitLabUser.class);
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GitLabUser that = (GitLabUser) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (avatarUrl != null ? !avatarUrl.equals(that.avatarUrl) : that.avatarUrl != null) return false;
        if (webUrl != null ? !webUrl.equals(that.webUrl) : that.webUrl != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (websiteUrl != null ? !websiteUrl.equals(that.websiteUrl) : that.websiteUrl != null) return false;
        if (organization != null ? !organization.equals(that.organization) : that.organization != null) return false;
        if (lastSignInAt != null ? !lastSignInAt.equals(that.lastSignInAt) : that.lastSignInAt != null) return false;
        if (confirmedAt != null ? !confirmedAt.equals(that.confirmedAt) : that.confirmedAt != null) return false;
        if (lastActivityOn != null ? !lastActivityOn.equals(that.lastActivityOn) : that.lastActivityOn != null)
            return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (projectsLimit != null ? !projectsLimit.equals(that.projectsLimit) : that.projectsLimit != null)
            return false;
        if (currentSignInAt != null ? !currentSignInAt.equals(that.currentSignInAt) : that.currentSignInAt != null)
            return false;
        if (canCreateGroup != null ? !canCreateGroup.equals(that.canCreateGroup) : that.canCreateGroup != null)
            return false;
        if (canCreateProject != null ? !canCreateProject.equals(that.canCreateProject) : that.canCreateProject != null)
            return false;
        return twoFactorEnabled != null ? twoFactorEnabled.equals(that.twoFactorEnabled) : that.twoFactorEnabled == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (webUrl != null ? webUrl.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (websiteUrl != null ? websiteUrl.hashCode() : 0);
        result = 31 * result + (organization != null ? organization.hashCode() : 0);
        result = 31 * result + (lastSignInAt != null ? lastSignInAt.hashCode() : 0);
        result = 31 * result + (confirmedAt != null ? confirmedAt.hashCode() : 0);
        result = 31 * result + (lastActivityOn != null ? lastActivityOn.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (projectsLimit != null ? projectsLimit.hashCode() : 0);
        result = 31 * result + (currentSignInAt != null ? currentSignInAt.hashCode() : 0);
        result = 31 * result + (canCreateGroup != null ? canCreateGroup.hashCode() : 0);
        result = 31 * result + (canCreateProject != null ? canCreateProject.hashCode() : 0);
        result = 31 * result + (twoFactorEnabled != null ? twoFactorEnabled.hashCode() : 0);
        return result;
    }
}
