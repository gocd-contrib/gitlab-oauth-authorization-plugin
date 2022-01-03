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
import cd.go.authorization.gitlab.client.models.AccessLevel;
import cd.go.authorization.gitlab.client.models.GitLabGroup;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.client.models.MembershipInfo;
import cd.go.authorization.gitlab.models.TokenInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class GroupMembershipCheckerTest {

    @Mock
    private GitLabUser gitLabUser;
    @Mock
    private TokenInfo tokenInfo;
    @Mock
    private GitLabClient gitLabClient;
    private GroupMembershipChecker groupMembershipChecker;

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
        groupMembershipChecker = new GroupMembershipChecker();
    }

    @Test
    public void shouldReturnTrueWhenUserIsAMemberOfGroup() throws Exception {
        final Map<String, List<String>> groupsFromRole = singletonMap("group-a", emptyList());
        final GitLabGroup gitLabGroup = mock(GitLabGroup.class);
        final List<GitLabGroup> gitLabGroups = asList(gitLabGroup);
        final String personalAccessToken = "some-random-token";

        when(gitLabGroup.getName()).thenReturn("group-a");

        final boolean member = groupMembershipChecker.memberOfAtLeastOneGroup(gitLabUser, personalAccessToken, gitLabClient, gitLabGroups, groupsFromRole);

        assertTrue(member);
        verifyNoMoreInteractions(gitLabClient);
    }

    @Test
    public void shouldCheckForAccessLevelWhenProvidedInRoleConfig() throws Exception {
        final Map<String, List<String>> groupsFromRole = singletonMap("group-a", asList("developer"));
        final GitLabGroup gitLabGroupA = mock(GitLabGroup.class);
        final GitLabGroup gitLabGroupB = mock(GitLabGroup.class);
        final List<GitLabGroup> gitLabGroups = asList(gitLabGroupB, gitLabGroupA);
        final MembershipInfo membershipInfo = mock(MembershipInfo.class);
        final String personalAccessToken = "some-random-token";

        when(gitLabGroupA.getName()).thenReturn("group-a");
        when(gitLabGroupB.getName()).thenReturn("group-b");
        when(gitLabClient.groupMembershipInfo(personalAccessToken, gitLabGroupA.getId(), gitLabUser.getId())).thenReturn(membershipInfo);
        when(membershipInfo.getAccessLevel()).thenReturn(AccessLevel.DEVELOPER);

        final boolean member = groupMembershipChecker.memberOfAtLeastOneGroup(gitLabUser, personalAccessToken, gitLabClient, gitLabGroups, groupsFromRole);

        assertTrue(member);
    }

}