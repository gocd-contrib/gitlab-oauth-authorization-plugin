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

package cd.go.authorization.gitlab.client;

import cd.go.authorization.gitlab.CallbackURL;
import cd.go.authorization.gitlab.client.models.GitLabGroup;
import cd.go.authorization.gitlab.client.models.GitLabProject;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.client.models.MembershipInfo;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.models.TokenInfo;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cd.go.authorization.gitlab.GitLabPlugin.LOG;
import static cd.go.authorization.gitlab.utils.Util.isNotBlank;
import static java.lang.String.valueOf;
import static java.text.MessageFormat.format;

public class GitLabClient {
    public static final String API_ERROR_MSG = "Api call to `{0}` failed with error: `{1}`";
    private final GitLabConfiguration gitLabConfiguration;
    private final OkHttpClient httpClient;

    public GitLabClient(GitLabConfiguration gitLabConfiguration) {
        this(gitLabConfiguration,
                new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()
        );
    }

    public GitLabClient(GitLabConfiguration gitLabConfiguration, OkHttpClient httpClient) {
        this.gitLabConfiguration = gitLabConfiguration;
        this.httpClient = httpClient;
    }

    public List<String> authorizationServerArgs(String callbackUrl) {
        String state = StateGenerator.generate();
        String authorizationServerUrl = HttpUrl.parse(gitLabConfiguration.gitLabBaseURL()).newBuilder()
                .addPathSegment("oauth")
                .addPathSegment("authorize")
                .addQueryParameter("client_id", gitLabConfiguration.applicationId())
                .addQueryParameter("redirect_uri", callbackUrl)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("scope", "api")
                .addQueryParameter("state", state)
                .build().toString();

        return List.of(authorizationServerUrl, state);
    }

    public TokenInfo fetchAccessToken(String code) throws IOException {

        final String accessTokenUrl = HttpUrl.parse(gitLabConfiguration.gitLabBaseURL())
                .newBuilder()
                .addPathSegment("oauth")
                .addPathSegment("token")
                .build().toString();

        final FormBody formBody = new FormBody.Builder()
                .add("client_id", gitLabConfiguration.applicationId())
                .add("client_secret", gitLabConfiguration.clientSecret())
                .add("code", code)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", CallbackURL.instance().getCallbackURL()).build();

        final Request request = new Request.Builder()
                .url(accessTokenUrl)
                .addHeader("Accept", "application/json")
                .post(formBody)
                .build();

        return executeRequest(request, response -> TokenInfo.fromJSON(response.body().string()));
    }

    public GitLabUser user(TokenInfo tokenInfo) throws IOException {
        LOG.info("Fetching gitlab user profile.");
        validateTokenInfo(tokenInfo);

        final String userProfileUrl = apiUrl(gitLabConfiguration.gitLabBaseURL(), tokenInfo.accessToken(), "user");

        final Request request = new Request.Builder().url(userProfileUrl).build();

        return executeRequest(request, response -> GitLabUser.fromJSON(response.body().string()));
    }

    public GitLabUser user(String personalAccessToken) throws IOException {
        LOG.info("Fetching gitlab user profile.");

        final String userProfileUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(), "user");

        final Request request = getRequestWithAccessToken(userProfileUrl,personalAccessToken);

        return executeRequest(request, response -> GitLabUser.fromJSON(response.body().string()));
    }

    public List<GitLabGroup> groups(String personalAccessToken) throws IOException {
        LOG.info("Fetching gitlab groups for a user.");

        final String groupsUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(),  "groups");
        final Request request = getRequestWithAccessToken(groupsUrl,personalAccessToken);

        return executeRequest(request, response -> GitLabGroup.fromJSONArray(response.body().string()));
    }

    public List<GitLabProject> projects(String personalAccessToken) throws IOException {
        LOG.info("Fetching gitlab projects for a user.");

        final String projectsUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(),  "projects");
        final Request request = getRequestWithAccessToken(projectsUrl,personalAccessToken);

        return executeRequest(request, response -> GitLabProject.fromJSONArray(response.body().string()));
    }

    public MembershipInfo groupMembershipInfo(String personalAccessToken, long groupId, long memberId) throws IOException {
        LOG.info(format("Fetching group membership info for member `{1}` to a group `{0}`.", groupId, memberId));

        final String membershipUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(), toPathArray("groups", groupId, "members", memberId));
        final Request request = getRequestWithAccessToken(membershipUrl, personalAccessToken);

        return executeRequest(request, response -> MembershipInfo.fromJSON(response.body().string()));
    }

    public MembershipInfo projectMembershipInfo(String personalAccessToken, long projectId, long memberId) throws IOException {
        LOG.info(format("Fetching group membership info for member `{1}` to a project `{0}`.", projectId, memberId));

        final String membershipUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(), toPathArray("projects", projectId, "members", memberId));
        final Request request = getRequestWithAccessToken(membershipUrl, personalAccessToken);

        return executeRequest(request, response -> MembershipInfo.fromJSON(response.body().string()));
    }

    private interface Callback<T> {
        T onResponse(Response response) throws IOException;
    }

    private String[] toPathArray(Object... objects) {
        final String[] paths = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                throw new RuntimeException("Path segment cannot be null.");
            }
            paths[i] = valueOf(objects[i]);
        }
        return paths;
    }

    private <T> T executeRequest(Request request, Callback<T> callback) throws IOException {
        final Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            final String responseBody = response.body().string();
            final String errorMessage = isNotBlank(responseBody) ? responseBody : response.message();
            throw new RuntimeException(format(API_ERROR_MSG, request.url().encodedPath(), errorMessage));
        }

        return callback.onResponse(response);
    }

    private void validateTokenInfo(TokenInfo tokenInfo) {
        if (tokenInfo == null) {
            throw new RuntimeException("TokenInfo must not be null.");
        }
    }

    private String apiUrl(String baseURL, String accessToken, String... paths) {
        final HttpUrl.Builder builder = HttpUrl.parse(baseURL)
                .newBuilder()
                .addPathSegment("api")
                .addPathSegment("v4")
                .addQueryParameter("access_token", accessToken);

        for (String path : paths) {
            builder.addPathSegment(path);
        }

        return builder.build().toString();
    }

    private String apiUrlWithPersonalAccessToken(String baseURL, String... paths) {
        final HttpUrl.Builder builder = HttpUrl.parse(baseURL)
                .newBuilder()
                .addPathSegment("api")
                .addPathSegment("v4");

        for (String path : paths) {
            builder.addPathSegment(path);
        }

        return builder.build().toString();
    }

    private Request getRequestWithAccessToken(String url, String personalAccessToken) {
        return new Request.Builder()
                .addHeader("Private-Token", personalAccessToken)
                .url(url)
                .build();
    }
}
