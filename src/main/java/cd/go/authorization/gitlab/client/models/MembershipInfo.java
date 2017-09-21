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

import static cd.go.authorization.gitlab.utils.Util.GSON;

public class MembershipInfo {
    @Expose
    @SerializedName("id")
    private long id;
    @Expose
    @SerializedName("username")
    private String username;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("state")
    private String state;
    @Expose
    @SerializedName("created_at")
    private String createdAt;
    @Expose
    @SerializedName("access_level")
    private AccessLevel accessLevel;

    MembershipInfo() {
    }

    public MembershipInfo(long id, String username, AccessLevel accessLevel) {
        this.id = id;
        this.username = username;
        this.accessLevel = accessLevel;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public static MembershipInfo fromJSON(String json) {
        return GSON.fromJson(json, MembershipInfo.class);
    }

    public String toJSON() {
        return GSON.toJson(this);
    }
}
