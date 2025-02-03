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
import cd.go.authorization.gitlab.utils.Util;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        ProofKey proofKey = new ProofKey();
        String authorizationServerUrl = HttpUrl.parse(gitLabConfiguration.gitLabBaseURL()).newBuilder()
                .addPathSegment("oauth")
                .addPathSegment("authorize")
                .addQueryParameter("client_id", gitLabConfiguration.applicationId())
                .addQueryParameter("redirect_uri", callbackUrl)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("scope", gitLabConfiguration.clientScopesRequested().stream().collect(Collectors.joining(" ")))
                .addQueryParameter("state", state)
                .addQueryParameter("code_challenge_method", "S256")
                .addEncodedQueryParameter("code_challenge", proofKey.codeChallengeEncoded())
                .build().toString();

        return List.of(authorizationServerUrl, state, proofKey.codeVerifierEncoded());
    }

    public TokenInfo fetchAccessToken(String code, String codeVerifierEncoded) throws IOException {

        final String accessTokenUrl = HttpUrl.parse(gitLabConfiguration.gitLabBaseURL())
                .newBuilder()
                .addPathSegment("oauth")
                .addPathSegment("token")
                .build().toString();

        final FormBody formBody = new FormBody.Builder()
                .add("client_id", gitLabConfiguration.applicationId())
                .add("client_secret", gitLabConfiguration.clientSecret())
                .add("grant_type", "authorization_code")
                .add("code", code)
                .addEncoded("code_verifier", codeVerifierEncoded)
                .add("redirect_uri", CallbackURL.instance().getCallbackURL())
                .build();

        final Request request = new Request.Builder()
                .url(accessTokenUrl)
                .addHeader("Accept", "application/json")
                .post(formBody)
                .build();

        return executeRequest(request, response -> TokenInfo.fromJSON(response.body().string()));
    }

    public GitLabUser user(TokenInfo tokenInfo) throws IOException {
        LOG.info("Fetching gitlab user profile (with OAuth2 access token).");
        validateTokenInfo(tokenInfo);

        final String userProfileUrl = apiUrl(gitLabConfiguration.gitLabBaseURL(), tokenInfo.accessToken(), "user");

        final Request request = new Request.Builder().url(userProfileUrl).build();

        return executeRequest(request, response -> GitLabUser.fromJSON(response.body().string()));
    }

    public GitLabUser user(String personalAccessToken) throws IOException {
        LOG.info("Fetching gitlab user profile (with personal/group access token).");

        final String userProfileUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(), "user");

        final Request request = getRequestWithAccessToken(userProfileUrl,personalAccessToken);

        return executeRequest(request, response -> GitLabUser.fromJSON(response.body().string()));
    }

    public List<GitLabGroup> groups(String personalAccessToken) throws IOException {
        LOG.info("Fetching gitlab groups for a user (with personal/group access token).");

        final String groupsUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(),  "groups");
        final Request request = getRequestWithAccessToken(groupsUrl,personalAccessToken);

        return executeRequestRepeated(request, response -> GitLabGroup.fromJSONArray(response.body().string()));
    }

    public List<GitLabProject> projects(String personalAccessToken) throws IOException {
        LOG.info("Fetching gitlab projects for a user (with personal/group access token).");

        final String projectsUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(),  "projects");
        final Request request = getRequestWithAccessToken(projectsUrl,personalAccessToken);

        return executeRequestRepeated(request, response -> GitLabProject.fromJSONArray(response.body().string()));
    }

    public MembershipInfo groupMembershipInfo(String personalAccessToken, long groupId, long memberId) throws IOException {
        LOG.info(format("Fetching group membership info for member `{1}` to a group `{0}`. (with personal/group access token)", groupId, memberId));

        final String membershipUrl = apiUrlWithPersonalAccessToken(gitLabConfiguration.gitLabBaseURL(), toPathArray("groups", groupId, "members", memberId));
        final Request request = getRequestWithAccessToken(membershipUrl, personalAccessToken);

        return executeRequest(request, response -> MembershipInfo.fromJSON(response.body().string()));
    }

    public MembershipInfo projectMembershipInfo(String personalAccessToken, long projectId, long memberId) throws IOException {
        LOG.info(format("Fetching group membership info for member `{1}` to a project `{0}`. (with personal/group access token)", projectId, memberId));

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
        return callback.onResponse(successfulResponseFor(request));
    }

    /**
     * Repeatedly execute the request until all pages are loaded
     */
    private <E, T extends List<E>> List<E> executeRequestRepeated(Request request, Callback<T> callback) throws IOException {
        HttpUrl originalUrl = request.url();
        List<E> result = new ArrayList<>();

        String nextPage = null;

        do {
            if (nextPage != null) {
                request = request.newBuilder()
                        .url(originalUrl.newBuilder().addQueryParameter("page", nextPage.trim()).build())
                        .build();
            }
            final Response response = successfulResponseFor(request);

            result.addAll(callback.onResponse(response));

            // Check if there are more pages to load
            nextPage = response.header("x-next-page");
        } while (Util.isNotBlank(nextPage));

        return result;
    }

    private Response successfulResponseFor(Request request) throws IOException {
        final Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            final String responseBody = response.body() != null ? response.body().string() : "";
            final String errorMessage = isNotBlank(responseBody) ? responseBody : response.message();
            throw new RuntimeException(format(API_ERROR_MSG, request.url().encodedPath(), errorMessage));
        }
        return response;
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
