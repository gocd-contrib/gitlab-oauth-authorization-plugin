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

package cd.go.authorization.gitlab.models;

import cd.go.authorization.gitlab.annotation.ProfileField;
import cd.go.authorization.gitlab.annotation.Validatable;
import cd.go.authorization.gitlab.client.GitLabClient;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import static cd.go.authorization.gitlab.utils.Util.GSON;

public class GitLabConfiguration implements Validatable {
    private static final String GITLAB_URL = "https://gitlab.com";

    @Expose
    @SerializedName("ApplicationId")
    @ProfileField(key = "ApplicationId", required = true, secure = false)
    private String applicationId;

    @Expose
    @SerializedName("ClientSecret")
    @ProfileField(key = "ClientSecret", required = true, secure = true)
    private String clientSecret;

    @Expose
    @SerializedName("AuthenticateWith")
    @ProfileField(key = "AuthenticateWith", required = false, secure = false)
    private AuthenticateWith authenticateWith;

    @Expose
    @SerializedName("GitLabEnterpriseUrl")
    @ProfileField(key = "GitLabEnterpriseUrl", required = false, secure = false)
    private String gitLabEnterpriseUrl;

    @Expose
    @SerializedName("PersonalAccessToken")
    @ProfileField(key = "PersonalAccessToken", required = true, secure = true)
    private String personalAccessToken;
    private GitLabClient gitLabClient;

    public GitLabConfiguration() {
    }

    public GitLabConfiguration(String applicationId, String clientSecret) {
        this(applicationId, clientSecret, AuthenticateWith.GITLAB, null, "");
    }

    public GitLabConfiguration(String applicationId, String clientSecret, AuthenticateWith authenticateWith, String gitLabEnterpriseUrl, String personalAccessToken) {
        this.applicationId = applicationId;
        this.clientSecret = clientSecret;
        this.authenticateWith = authenticateWith;
        this.gitLabEnterpriseUrl = gitLabEnterpriseUrl;
        this.personalAccessToken = personalAccessToken;
    }

    public String personalAccessToken() {
        return personalAccessToken;
    }

    public String applicationId() {
        return applicationId;
    }

    public String clientSecret() {
        return clientSecret;
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public AuthenticateWith authenticateWith() {
        return authenticateWith;
    }

    public String gitLabEnterpriseUrl() {
        return gitLabEnterpriseUrl;
    }

    public String gitLabBaseURL() {
        return authenticateWith == AuthenticateWith.GITLAB ? GITLAB_URL : gitLabEnterpriseUrl;
    }

    public static GitLabConfiguration fromJSON(String json) {
        return GSON.fromJson(json, GitLabConfiguration.class);
    }

    public Map<String, String> toProperties() {
        return GSON.fromJson(toJSON(), new TypeToken<Map<String, String>>() {
        }.getType());
    }

    public GitLabClient gitLabClient() {
        if (gitLabClient == null) {
            gitLabClient = new GitLabClient(this);
        }

        return gitLabClient;
    }
}
