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

package cd.go.authorization.gitlab.client;

import cd.go.authorization.gitlab.CallbackURL;
import cd.go.authorization.gitlab.client.models.*;
import cd.go.authorization.gitlab.models.AuthenticateWith;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.models.TokenInfo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.util.List;

import static cd.go.authorization.gitlab.utils.Util.GSON;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GitLabClientTest {

    @Mock
    private GitLabConfiguration gitLabConfiguration;
    private MockWebServer server;
    private GitLabClient gitLabClient;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        server = new MockWebServer();
        server.start();

        when(gitLabConfiguration.applicationId()).thenReturn("client-id");
        when(gitLabConfiguration.clientSecret()).thenReturn("client-secret");
        when(gitLabConfiguration.authenticateWith()).thenReturn(AuthenticateWith.GITLAB);
        when(gitLabConfiguration.gitLabBaseURL()).thenReturn("https://gitlab.com");

        CallbackURL.instance().updateRedirectURL("callback-url");

        gitLabClient = new GitLabClient(gitLabConfiguration);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void shouldReturnAuthorizationServerUrlForGitLab() throws Exception {
        final String authorizationServerUrl = gitLabClient.authorizationServerUrl("call-back-url");

        assertThat(authorizationServerUrl, startsWith("https://gitlab.com/oauth/authorize?client_id=client-id&redirect_uri=call-back-url&response_type=code&scope=api&state="));
    }

    @Test
    public void shouldReturnAuthorizationServerUrlForGitLabEnterprise() throws Exception {
        when(gitLabConfiguration.authenticateWith()).thenReturn(AuthenticateWith.GITLAB_ENTERPRISE);
        when(gitLabConfiguration.gitLabBaseURL()).thenReturn("http://enterprise.url");

        final String authorizationServerUrl = gitLabClient.authorizationServerUrl("call-back-url");

        assertThat(authorizationServerUrl, startsWith("http://enterprise.url/oauth/authorize?client_id=client-id&redirect_uri=call-back-url&response_type=code&scope=api&state="));
    }

    @Test
    public void shouldFetchTokenInfoUsingAuthorizationCode() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token").toJSON()));

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final TokenInfo tokenInfo = gitLabClient.fetchAccessToken("code");


        assertThat(tokenInfo.accessToken(), is("token-444248275346-5758603453985735"));

        RecordedRequest request = server.takeRequest();
        assertEquals("POST /oauth/token HTTP/1.1", request.getRequestLine());
        assertEquals("application/x-www-form-urlencoded", request.getHeader("Content-Type"));
        assertEquals("client_id=client-id&client_secret=client-secret&code=code&grant_type=authorization_code&redirect_uri=callback-url", request.getBody().readUtf8());
    }

    @Test
    public void shouldFetchUserProfile() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new GitLabUser("username", "Display Name", "email").toJSON()));

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final GitLabUser gitLabUser = gitLabClient.user(tokenInfo);

        assertThat(gitLabUser.getUsername(), is("username"));

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/user?access_token=token-444248275346-5758603453985735 HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void shouldFetchGroupsForAUser() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(GSON.toJson(asList(new GitLabGroup(1L, "foo-group")))));

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final List<GitLabGroup> gitLabGroups = gitLabClient.groups(tokenInfo);

        assertThat(gitLabGroups, hasSize(1));
        assertThat(gitLabGroups.get(0).getName(), is("foo-group"));

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/groups?access_token=token-444248275346-5758603453985735 HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void shouldFetchProjectsForAUser() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(GSON.toJson(asList(new GitLabProject(1L, "foo-project")))));

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final List<GitLabProject> gitLabProjects = gitLabClient.projects(tokenInfo);

        assertThat(gitLabProjects, hasSize(1));
        assertThat(gitLabProjects.get(0).getName(), is("foo-project"));

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/projects?access_token=token-444248275346-5758603453985735 HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void shouldFetchGroupMembershipForAUser() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token");

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new MembershipInfo(1L, "foo-user", AccessLevel.DEVELOPER).toJSON()));

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final MembershipInfo membershipInfo = gitLabClient.groupMembershipInfo(tokenInfo, 1L, 1L);

        assertThat(membershipInfo.getUsername(), is("foo-user"));
        assertThat(membershipInfo.getAccessLevel(), is(AccessLevel.DEVELOPER));

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/groups/1/members/1?access_token=token-444248275346-5758603453985735 HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void shouldFetchProjectMembershipForAUser() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token");

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new MembershipInfo(1L, "foo-user", AccessLevel.DEVELOPER).toJSON()));

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        final MembershipInfo membershipInfo = gitLabClient.projectMembershipInfo(tokenInfo, 1L, 1L);

        assertThat(membershipInfo.getUsername(), is("foo-user"));
        assertThat(membershipInfo.getAccessLevel(), is(AccessLevel.DEVELOPER));

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /api/v4/projects/1/members/1?access_token=token-444248275346-5758603453985735 HTTP/1.1", request.getRequestLine());
    }

    @Test
    public void shouldErrorOutWhenAPIRequestFails() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token");

        server.enqueue(new MockResponse().setResponseCode(403).setBody("Unauthorized"));

        when(gitLabConfiguration.gitLabBaseURL()).thenReturn(server.url("/").toString());

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Api call to `/api/v4/user` failed with error: `Unauthorized`");

        gitLabClient.user(tokenInfo);
    }
}