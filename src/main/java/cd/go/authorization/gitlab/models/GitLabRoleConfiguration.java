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

package cd.go.authorization.gitlab.models;

import cd.go.authorization.gitlab.annotation.ProfileField;
import cd.go.authorization.gitlab.annotation.Validatable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cd.go.authorization.gitlab.utils.Util.*;

public class GitLabRoleConfiguration implements Validatable {

    @Expose
    @SerializedName("Groups")
    @ProfileField(key = "Groups", required = false, secure = false)
    private String groups;

    @Expose
    @SerializedName("Projects")
    @ProfileField(key = "Projects", required = false, secure = false)
    private String projects;

    @Expose
    @SerializedName("Users")
    @ProfileField(key = "Users", required = false, secure = false)
    private String users;

    public GitLabRoleConfiguration() {
    }

    public Map<String, List<String>> groups() {
        return toMap(groups);
    }

    public Map<String, List<String>> projects() {
        return toMap(projects);
    }

    private HashMap<String, List<String>> toMap(String string) {
        final HashMap<String, List<String>> map = new HashMap<>();
        splitIntoLinesAndTrimSpaces(string).forEach(line -> {
            if (line.contains(":")) {
                final String[] parts = line.split(":", 2);
                map.put(parts[0], listFromCommaSeparatedString(toLowerCase(parts[1])));
            } else {
                map.put(line, Collections.emptyList());
            }
        });
        return map;
    }

    public static GitLabRoleConfiguration fromJSON(String json) {
        return GSON.fromJson(json, GitLabRoleConfiguration.class);
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public Map<String, String> toProperties() {
        return GSON.fromJson(toJSON(), new TypeToken<Map<String, String>>() {
        }.getType());
    }

    public boolean hasConfiguration() {
        return isNotBlank(groups) || isNotBlank(projects) || isNotBlank(users);
    }

    public List<String> users() {
        return listFromCommaSeparatedString(users);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GitLabRoleConfiguration that = (GitLabRoleConfiguration) o;

        if (groups != null ? !groups.equals(that.groups) : that.groups != null) return false;
        if (projects != null ? !projects.equals(that.projects) : that.projects != null) return false;
        return users != null ? users.equals(that.users) : that.users == null;
    }

    @Override
    public int hashCode() {
        int result = groups != null ? groups.hashCode() : 0;
        result = 31 * result + (projects != null ? projects.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }
}
