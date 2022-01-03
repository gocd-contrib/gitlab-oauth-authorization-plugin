/*
 * Copyright 2019 ThoughtWorks, Inc.
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

package cd.go.authorization.gitlab.executors;

import cd.go.authorization.gitlab.GitLabAuthorizer;
import cd.go.authorization.gitlab.client.GitLabClient;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.models.AuthConfig;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.models.GitLabRole;
import cd.go.authorization.gitlab.requests.GetRolesRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GetRolesExecutorTest {
    private GetRolesRequest request;
    private GetRolesExecutor executor;

    private GitLabAuthorizer authorizer;
    private GitLabClient gitLabClient;
    private GitLabUser gitLabUser;
    private final String PERSONAL_ACCESS_TOKEN = "some-random-token";

    @BeforeEach
    public void setUp() throws Exception {
        request = mock(GetRolesRequest.class);
        authorizer = mock(GitLabAuthorizer.class);

        AuthConfig authConfig = mock(AuthConfig.class);
        gitLabClient = mock(GitLabClient.class);
        gitLabUser = mock(GitLabUser.class);
        when(request.getAuthConfig()).thenReturn(authConfig);
        when(request.getUsername()).thenReturn("bob");
        GitLabConfiguration gitLabConfiguration = mock(GitLabConfiguration.class);
        when(authConfig.gitLabConfiguration()).thenReturn(gitLabConfiguration);
        when(gitLabConfiguration.gitLabClient()).thenReturn(gitLabClient);
        when(gitLabConfiguration.personalAccessToken()).thenReturn(PERSONAL_ACCESS_TOKEN);

        executor = new GetRolesExecutor(request, authorizer);
    }

    @Test
    public void shouldReturnEmptyResponseIfThereAreNoRolesProvidedFromRequest() throws Exception {

        when(gitLabClient.user(anyString())).thenReturn(gitLabUser);

        GoPluginApiResponse response = executor.execute();

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals("[]", response.responseBody(), true);
        verifyNoInteractions(authorizer);
    }

    @Test
    public void shouldReturnSuccessResponseWithRoles() throws Exception {
        when(gitLabClient.user(PERSONAL_ACCESS_TOKEN)).thenReturn(gitLabUser);
        when(request.getRoles()).thenReturn(rolesWithName("blackbird", "super-admin", "view"));
        when(authorizer.authorize(gitLabUser, request.getAuthConfig(), request.getRoles())).thenReturn(Arrays.asList("blackbird", "super-admin"));

        GoPluginApiResponse response = executor.execute();

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals("[\"blackbird\",\"super-admin\"]", response.responseBody(), true);

        verify(gitLabClient).user(request.getAuthConfig().gitLabConfiguration().personalAccessToken());
        verify(authorizer).authorize(gitLabUser, request.getAuthConfig(), request.getRoles());
    }

    @Test
    public void shouldReturnErrorResponseWhenUserWithProvidedUsernameNotFound() throws Exception {
        when(request.getRoles()).thenReturn(rolesWithName("blackbird", "super-admin", "view"));

        GoPluginApiResponse response = executor.execute();

        assertThat(response.responseCode()).isEqualTo(500);
        verify(gitLabClient).user(request.getAuthConfig().gitLabConfiguration().personalAccessToken());
        verifyNoInteractions(authorizer);
    }

    private GitLabRole roleWithName(String name) {
        return GitLabRole.fromJSON("{\"name\":\"" + name + "\"}");
    }

    private List<GitLabRole> rolesWithName(String... names) {
        return Arrays.stream(names).map(this::roleWithName).collect(Collectors.toList());
    }
}