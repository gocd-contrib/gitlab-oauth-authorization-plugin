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

package cd.go.authorization.gitlab;

import cd.go.authorization.gitlab.client.GitLabClient;
import cd.go.authorization.gitlab.client.models.GitLabGroup;
import cd.go.authorization.gitlab.client.models.GitLabProject;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GitLabAuthorizerTest {
    private GroupMembershipChecker groupMembershipChecker;
    private GitLabUser gitLabUser;
    private TokenInfo tokenInfo;
    private AuthConfig authConfig;
    private GitLabAuthorizer gitLabAuthorizer;
    private GitLabConfiguration gitLabConfiguration;
    private GitLabClient gitLabClient;
    private ProjectMembershipChecker projectMembershipChecker;

    @BeforeEach
    public void setUp() throws Exception {
        groupMembershipChecker = mock(GroupMembershipChecker.class);
        gitLabUser = mock(GitLabUser.class);
        tokenInfo = mock(TokenInfo.class);
        authConfig = mock(AuthConfig.class);
        gitLabConfiguration = mock(GitLabConfiguration.class);
        gitLabClient = mock(GitLabClient.class);
        projectMembershipChecker = mock(ProjectMembershipChecker.class);

        when(authConfig.gitLabConfiguration()).thenReturn(gitLabConfiguration);
        when(gitLabConfiguration.gitLabClient()).thenReturn(gitLabClient);

        gitLabAuthorizer = new GitLabAuthorizer(groupMembershipChecker, projectMembershipChecker);
    }

    @Test
    public void shouldAssignRoleWhenUsernameIsWhiteListed() throws Exception {
        final GitLabRole gitLabRole = mock(GitLabRole.class);
        final GitLabRoleConfiguration gitLabRoleConfiguration = mock(GitLabRoleConfiguration.class);

        when(gitLabRole.roleConfiguration()).thenReturn(gitLabRoleConfiguration);
        when(gitLabRoleConfiguration.users()).thenReturn(asList("bob"));
        when(gitLabUser.getUsername()).thenReturn("bob");
        when(gitLabRole.name()).thenReturn("admin");

        final List<String> roles = gitLabAuthorizer.authorize(gitLabUser, authConfig, asList(gitLabRole));

        assertThat(roles).hasSize(1);
        assertThat(roles).contains("admin");
    }

    @Test
    public void shouldAssignRoleWhenUserIsAMemberOfAGroup() throws Exception {
        final GitLabRole gitLabRole = mock(GitLabRole.class);
        final GitLabRoleConfiguration gitLabRoleConfiguration = mock(GitLabRoleConfiguration.class);
        final List<GitLabGroup> gitLabGroups = asList(mock(GitLabGroup.class));
        final Map<String, List<String>> groups = singletonMap("group-a", emptyList());
        final String personalAccessToken = "some-random-token";

        when(gitLabClient.groups(personalAccessToken)).thenReturn(gitLabGroups);
        when(gitLabRole.name()).thenReturn("admin");
        when(gitLabRole.roleConfiguration()).thenReturn(gitLabRoleConfiguration);
        when(authConfig.gitLabConfiguration().personalAccessToken()).thenReturn(personalAccessToken);
        when(gitLabRoleConfiguration.groups()).thenReturn(groups);
        when(groupMembershipChecker.memberOfAtLeastOneGroup(gitLabUser, personalAccessToken, gitLabClient, gitLabGroups, groups)).thenReturn(true);

        final List<String> roles = gitLabAuthorizer.authorize(gitLabUser, authConfig, asList(gitLabRole));

        assertThat(roles).hasSize(1);
        assertThat(roles).contains("admin");
    }

    @Test
    public void shouldAssignRoleWhenUserIsAMemberOfAProject() throws Exception {
        final GitLabRole gitLabRole = mock(GitLabRole.class);
        final GitLabRoleConfiguration gitLabRoleConfiguration = mock(GitLabRoleConfiguration.class);
        final List<GitLabGroup> gitLabGroups = asList(mock(GitLabGroup.class));
        final List<GitLabProject> gitLabProjects = asList(mock(GitLabProject.class));
        final Map<String, List<String>> projects = singletonMap("project-foo", emptyList());
        final String personalAccessToken = "some-random-token";

        when(gitLabClient.projects(personalAccessToken)).thenReturn(gitLabProjects);
        when(gitLabRole.name()).thenReturn("admin");
        when(gitLabRole.roleConfiguration()).thenReturn(gitLabRoleConfiguration);
        when(authConfig.gitLabConfiguration().personalAccessToken()).thenReturn(personalAccessToken);
        when(gitLabRoleConfiguration.projects()).thenReturn(projects);
        when(groupMembershipChecker.memberOfAtLeastOneGroup(gitLabUser, personalAccessToken, gitLabClient, gitLabGroups, gitLabRoleConfiguration.groups())).thenReturn(false);

        when(projectMembershipChecker.memberOfAtLeastOneProject(gitLabUser, personalAccessToken, gitLabClient, gitLabProjects, projects)).thenReturn(true);

        final List<String> roles = gitLabAuthorizer.authorize(gitLabUser, authConfig, asList(gitLabRole));

        verify(projectMembershipChecker).memberOfAtLeastOneProject(any(), anyString(), any(), anyList(), anyMap());
        assertThat(roles).hasSize(1);
        assertThat(roles).contains("admin");
    }

}