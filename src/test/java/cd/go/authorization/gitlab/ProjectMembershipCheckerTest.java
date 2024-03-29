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

package cd.go.authorization.gitlab;

import cd.go.authorization.gitlab.client.GitLabClient;
import cd.go.authorization.gitlab.client.models.AccessLevel;
import cd.go.authorization.gitlab.client.models.GitLabProject;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.client.models.MembershipInfo;
import cd.go.authorization.gitlab.models.TokenInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ProjectMembershipCheckerTest {
    public static final String PERSONAL_ACCESS_TOKEN = "some-access-token";
    @Mock
    private GitLabUser gitLabUser;
    @Mock
    private TokenInfo tokenInfo;
    @Mock
    private GitLabClient gitLabClient;
    private ProjectMembershipChecker projectMembershipChecker;

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
        projectMembershipChecker = new ProjectMembershipChecker();
    }

    @Test
    public void shouldReturnTrueWhenUserIsAMemberOfGroup() throws Exception {
        final Map<String, List<String>> groupsFromRole = singletonMap("project-duck-simulator", emptyList());
        final GitLabProject gitLabProject = mock(GitLabProject.class);
        final List<GitLabProject> gitLabProjects = List.of(gitLabProject);

        when(gitLabProject.getName()).thenReturn("project-duck-simulator");

        final boolean member = projectMembershipChecker.memberOfAtLeastOneProject(gitLabUser, PERSONAL_ACCESS_TOKEN, gitLabClient, gitLabProjects, groupsFromRole);

        assertTrue(member);
        verifyNoMoreInteractions(gitLabClient);
    }

    @Test
    public void shouldCheckForAccessLevelWhenProvidedInRoleConfig() throws Exception {
        final Map<String, List<String>> groupsFromRole = singletonMap("project-duck-simulator", List.of("developer"));
        final GitLabProject gitLabProjectA = mock(GitLabProject.class);
        final GitLabProject gitLabProjectB = mock(GitLabProject.class);
        final List<GitLabProject> gitLabProjects = List.of(gitLabProjectB, gitLabProjectA);
        final MembershipInfo membershipInfo = mock(MembershipInfo.class);

        when(gitLabProjectA.getName()).thenReturn("project-duck-simulator");
        when(gitLabProjectB.getName()).thenReturn("project-sudoku-solver");
        when(gitLabClient.projectMembershipInfo(PERSONAL_ACCESS_TOKEN, gitLabProjectA.getId(), gitLabUser.getId())).thenReturn(membershipInfo);
        when(membershipInfo.getAccessLevel()).thenReturn(AccessLevel.DEVELOPER);

        final boolean member = projectMembershipChecker.memberOfAtLeastOneProject(gitLabUser, PERSONAL_ACCESS_TOKEN, gitLabClient, gitLabProjects, groupsFromRole);

        assertTrue(member);
    }

}