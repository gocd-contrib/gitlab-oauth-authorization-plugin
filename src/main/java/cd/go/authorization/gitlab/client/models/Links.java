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

public class Links {

    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("issues")
    @Expose
    private String issues;
    @SerializedName("merge_requests")
    @Expose
    private String mergeRequests;
    @SerializedName("repo_branches")
    @Expose
    private String repoBranches;
    @SerializedName("labels")
    @Expose
    private String labels;
    @SerializedName("events")
    @Expose
    private String events;
    @SerializedName("members")
    @Expose
    private String members;

    Links() {
    }

    public String getEvents() {
        return events;
    }

    public String getSelf() {
        return self;
    }

    public String getIssues() {
        return issues;
    }

    public String getMergeRequests() {
        return mergeRequests;
    }

    public String getRepoBranches() {
        return repoBranches;
    }

    public String getLabels() {
        return labels;
    }

    public String getMembers() {
        return members;
    }
}
