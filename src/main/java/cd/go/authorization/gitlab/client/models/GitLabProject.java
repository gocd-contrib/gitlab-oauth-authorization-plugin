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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static cd.go.authorization.gitlab.utils.Util.GSON;

public class GitLabProject {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("default_branch")
    @Expose
    private String defaultBranch;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("ssh_url_to_repo")
    @Expose
    private String sshUrlToRepo;
    @SerializedName("http_url_to_repo")
    @Expose
    private String httpUrlToRepo;
    @SerializedName("web_url")
    @Expose
    private String webUrl;
    @SerializedName("tag_list")
    @Expose
    private List<String> tagList = null;
    @SerializedName("owner")
    @Expose
    private Owner owner;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_with_namespace")
    @Expose
    private String nameWithNamespace;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("path_with_namespace")
    @Expose
    private String pathWithNamespace;
    @SerializedName("issues_enabled")
    @Expose
    private boolean issuesEnabled;
    @SerializedName("open_issues_count")
    @Expose
    private long openIssuesCount;
    @SerializedName("merge_requests_enabled")
    @Expose
    private boolean mergeRequestsEnabled;
    @SerializedName("jobs_enabled")
    @Expose
    private boolean jobsEnabled;
    @SerializedName("wiki_enabled")
    @Expose
    private boolean wikiEnabled;
    @SerializedName("snippets_enabled")
    @Expose
    private boolean snippetsEnabled;
    @SerializedName("resolve_outdated_diff_discussions")
    @Expose
    private boolean resolveOutdatedDiffDiscussions;
    @SerializedName("container_registry_enabled")
    @Expose
    private boolean containerRegistryEnabled;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("last_activity_at")
    @Expose
    private String lastActivityAt;
    @SerializedName("creator_id")
    @Expose
    private long creatorId;
    @SerializedName("namespace")
    @Expose
    private Namespace namespace;
    @SerializedName("import_status")
    @Expose
    private String importStatus;
    @SerializedName("archived")
    @Expose
    private boolean archived;
    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;
    @SerializedName("shared_runners_enabled")
    @Expose
    private boolean sharedRunnersEnabled;
    @SerializedName("forks_count")
    @Expose
    private long forksCount;
    @SerializedName("star_count")
    @Expose
    private long starCount;
    @SerializedName("runners_token")
    @Expose
    private String runnersToken;
    @SerializedName("public_jobs")
    @Expose
    private boolean publicJobs;
    @SerializedName("shared_with_groups")
    @Expose
    private List<SharedWithGroup> sharedWithGroups = null;
    @SerializedName("only_allow_merge_if_pipeline_succeeds")
    @Expose
    private boolean onlyAllowMergeIfPipelineSucceeds;
    @SerializedName("only_allow_merge_if_all_discussions_are_resolved")
    @Expose
    private boolean onlyAllowMergeIfAllDiscussionsAreResolved;
    @SerializedName("request_access_enabled")
    @Expose
    private boolean requestAccessEnabled;
    @SerializedName("approvals_before_merge")
    @Expose
    private long approvalsBeforeMerge;
    @SerializedName("statistics")
    @Expose
    private Statistics statistics;
    @SerializedName("_links")
    @Expose
    private Links links;

    GitLabProject() {
    }

    public GitLabProject(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getApprovalsBeforeMerge() {
        return approvalsBeforeMerge;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getSshUrlToRepo() {
        return sshUrlToRepo;
    }

    public String getHttpUrlToRepo() {
        return httpUrlToRepo;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getNameWithNamespace() {
        return nameWithNamespace;
    }

    public String getPath() {
        return path;
    }

    public String getPathWithNamespace() {
        return pathWithNamespace;
    }

    public boolean getIssuesEnabled() {
        return issuesEnabled;
    }

    public long getOpenIssuesCount() {
        return openIssuesCount;
    }

    public boolean isMergeRequestsEnabled() {
        return mergeRequestsEnabled;
    }

    public boolean isJobsEnabled() {
        return jobsEnabled;
    }

    public boolean isWikiEnabled() {
        return wikiEnabled;
    }

    public boolean isSnippetsEnabled() {
        return snippetsEnabled;
    }

    public boolean isResolveOutdatedDiffDiscussions() {
        return resolveOutdatedDiffDiscussions;
    }

    public boolean isContainerRegistryEnabled() {
        return containerRegistryEnabled;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getLastActivityAt() {
        return lastActivityAt;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public String getImportStatus() {
        return importStatus;
    }

    public boolean isArchived() {
        return archived;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isSharedRunnersEnabled() {
        return sharedRunnersEnabled;
    }

    public long getForksCount() {
        return forksCount;
    }

    public long getStarCount() {
        return starCount;
    }

    public String getRunnersToken() {
        return runnersToken;
    }

    public boolean isPublicJobs() {
        return publicJobs;
    }

    public List<SharedWithGroup> getSharedWithGroups() {
        return sharedWithGroups;
    }

    public boolean isOnlyAllowMergeIfPipelineSucceeds() {
        return onlyAllowMergeIfPipelineSucceeds;
    }

    public boolean isOnlyAllowMergeIfAllDiscussionsAreResolved() {
        return onlyAllowMergeIfAllDiscussionsAreResolved;
    }

    public boolean isRequestAccessEnabled() {
        return requestAccessEnabled;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public Links getLinks() {
        return links;
    }

    public static GitLabProject fromJSON(String json) {
        return GSON.fromJson(json, GitLabProject.class);
    }

    public static List<GitLabProject> fromJSONArray(String json) {
        final Type type = new TypeToken<List<GitLabProject>>() {
        }.getType();
        return GSON.fromJson(json, type);
    }
}

