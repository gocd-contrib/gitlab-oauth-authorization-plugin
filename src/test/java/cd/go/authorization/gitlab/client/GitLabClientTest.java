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
import cd.go.authorization.gitlab.client.models.*;
import cd.go.authorization.gitlab.models.AuthenticateWith;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.models.TokenInfo;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import mockwebserver3.junit5.internal.MockWebServerExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.util.List;

import static cd.go.authorization.gitlab.utils.Util.GSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockWebServerExtension.class)
public class GitLabClientTest {

    @Mock
    private GitLabConfiguration gitLabConfiguration;
    private GitLabClient gitLabClient;

    private MockWebServer server;

    @BeforeEach
    public void setUp(MockWebServer server) throws Exception {
        this.server = server;
        openMocks(this);

        when(gitLabConfiguration.applicationId()).thenReturn("client-id");
        when(gitLabConfiguration.clientSecret()).thenReturn("client-secret");
        when(gitLabConfiguration.authenticateWith()).thenReturn(AuthenticateWith.GITLAB);
        when(gitLabConfiguration.gitLabBaseURL()).thenReturn("https://gitlab.com");

        CallbackURL.instance().updateRedirectURL("callback-url");

        gitLabClient = new GitLabClient(gitLabConfiguration);
    }

    @Test
    public void shouldReturnAuthorizationServerUrlForGitLab() throws Exception {
        final String authorizationServerUrl = gitLabClient.authorizationServerUrl("call-back-url");

        assertThat(authorizationServerUrl).startsWith("https://gitlab.com/oauth/authorize?client_id=client-id&redirect_uri=call-back-url&response_type=code&scope=api&state=");
    }

    @Test
    public void shouldReturnAuthorizationServerUrlForGitLabEnterprise() throws Exception {
        when(gitLabConfiguration.authenticateWith()).thenReturn(AuthenticateWith.GITLAB_ENTERPRISE);
        when(gitLabConfiguration.gitLabBaseURL()).thenReturn("http://enterprise.url");

        final String authorizationServerUrl = gitLabClient.authorizationServerUrl("call-back-url");

        assertThat(authorizationServerUrl).startsWith("http://enterprise.url/oauth/authorize?client_id=client-id&redirect_uri=call-back-url&response_type=code&scope=api&state=");
    }

    @Test
    public void shouldFetchTokenInfoUsingAuthorizationCode() throws Exception {
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .body(new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token").toJSON())
                .build()
        );

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final TokenInfo tokenInfo = gitLabClient.fetchAccessToken("code");


        assertThat(tokenInfo.accessToken()).isEqualTo("token-444248275346-5758603453985735");

        RecordedRequest request = server.takeRequest();
        assertEquals("POST /oauth/token HTTP/1.1", request.getRequestLine());
        assertEquals("application/x-www-form-urlencoded", request.getHeaders().get("Content-Type"));
        assertEquals("client_id=client-id&client_secret=client-secret&code=code&grant_type=authorization_code&redirect_uri=callback-url", request.getBody().readUtf8());
    }

    @Test
    public void shouldFetchUserProfile() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token");
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .body(new GitLabUser("username", "Display Name", "email").toJSON())
                .build());

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final GitLabUser gitLabUser = gitLabClient.user(tokenInfo);

        assertThat(gitLabUser.getUsername()).isEqualTo("username");

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/user?access_token=token-444248275346-5758603453985735 HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void shouldFetchGroupsForAUser() throws Exception {
        final String personalAccessToken = "some-random-token";
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .body(GSON.toJson(List.of(new GitLabGroup(1L, "foo-group"))))
                .build());

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final List<GitLabGroup> gitLabGroups = gitLabClient.groups(personalAccessToken);

        assertThat(gitLabGroups).hasSize(1);
        assertThat(gitLabGroups.get(0).getName()).isEqualTo("foo-group");

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/groups HTTP/1.1", request.getRequestLine());
        assertNotNull(request.getHeaders().get("Private-Token"));
        assertEquals(personalAccessToken, request.getHeaders().get("Private-Token"));
    }

    @Test
    public void shouldFetchProjectsForAUser() throws Exception {
        final String personalAccessToken = "some-random-token";
        server.enqueue(new MockResponse.Builder()
                .code(200)
                .body(GSON.toJson(List.of(new GitLabProject(1L, "foo-project"))))
                .build());

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final List<GitLabProject> gitLabProjects = gitLabClient.projects(personalAccessToken);

        assertThat(gitLabProjects).hasSize(1);
        assertThat(gitLabProjects.get(0).getName()).isEqualTo("foo-project");

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/projects HTTP/1.1", request.getRequestLine());
        assertNotNull(request.getHeaders().get("Private-Token"));
        assertEquals(personalAccessToken, request.getHeaders().get("Private-Token"));
    }

    @Test
    public void shouldFetchGroupMembershipForAUser() throws Exception {
        final String personalAccessToken = "some-random-token";

        server.enqueue(new MockResponse.Builder()
                .code(200)
                .body(new MembershipInfo(1L, "foo-user", AccessLevel.DEVELOPER).toJSON())
                .build());

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final MembershipInfo membershipInfo = gitLabClient.groupMembershipInfo(personalAccessToken, 1L, 1L);

        assertThat(membershipInfo.getUsername()).isEqualTo("foo-user");
        assertThat(membershipInfo.getAccessLevel()).isEqualTo(AccessLevel.DEVELOPER);

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/groups/1/members/1 HTTP/1.1", request.getRequestLine());
        assertNotNull(request.getHeaders().get("Private-Token"));
        assertEquals(personalAccessToken, request.getHeaders().get("Private-Token"));
    }

    @Test
    public void shouldFetchProjectMembershipForAUser() throws Exception {
        final String personalAccessToken = "some-random-token";

        server.enqueue(new MockResponse.Builder()
                .code(200)
                .body(new MembershipInfo(1L, "foo-user", AccessLevel.DEVELOPER).toJSON())
                .build());

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final MembershipInfo membershipInfo = gitLabClient.projectMembershipInfo(personalAccessToken, 1L, 1L);

        assertThat(membershipInfo.getUsername()).isEqualTo("foo-user");
        assertThat(membershipInfo.getAccessLevel()).isEqualTo(AccessLevel.DEVELOPER);

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/projects/1/members/1 HTTP/1.1", request.getRequestLine());
        assertNotNull(request.getHeaders().get("Private-Token"));
        assertEquals(personalAccessToken, request.getHeaders().get("Private-Token"));
    }

    @Test
    public void shouldErrorOutWhenAPIRequestFails() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token");

        server.enqueue(new MockResponse.Builder().code(403).body("Unauthorized").build());

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        assertThatThrownBy(() -> gitLabClient.user(tokenInfo))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Api call to `/api/v4/user` failed with error: `Unauthorized`");
    }
}