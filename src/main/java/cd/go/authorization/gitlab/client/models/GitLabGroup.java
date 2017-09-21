/*
 * Copyright 2017 ThoughtWorks, Inc.
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static cd.go.authorization.gitlab.utils.Util.GSON;

public class GitLabGroup {
    @Expose
    @SerializedName("id")
    private long id;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("path")
    private String path;
    @Expose
    @SerializedName("description")
    private String description;
    @Expose
    @SerializedName("visibility")
    private String visibility;
    @Expose
    @SerializedName("lfs_enabled")
    private boolean lfsEnabled;
    @Expose
    @SerializedName("web_url")
    private String webUrl;
    @Expose
    @SerializedName("request_access_enabled")
    private Boolean requestAccessEnabled;
    @Expose
    @SerializedName("full_name")
    private String fullName;
    @Expose
    @SerializedName("full_path")
    private String fullPath;
    @Expose
    @SerializedName("avatar_url")
    private String avatarUrl;

    GitLabGroup() {
    }

    public GitLabGroup(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public String getVisibility() {
        return visibility;
    }

    public Boolean getLfsEnabled() {
        return lfsEnabled;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Boolean getRequestAccessEnabled() {
        return requestAccessEnabled;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public static GitLabGroup fromJSON(String json) {
        return GSON.fromJson(json, GitLabGroup.class);
    }

    public static List<GitLabGroup> fromJSONArray(String json) {
        final Type type = new TypeToken<List<GitLabGroup>>() {
        }.getType();
        return GSON.fromJson(json, type);
    }
}
