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
import cd.go.authorization.gitlab.client.models.GitLabGroup;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.client.models.MembershipInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cd.go.authorization.gitlab.GitLabPlugin.LOG;
import static java.text.MessageFormat.format;

public class GroupMembershipChecker {

    public boolean memberOfAtLeastOneGroup(GitLabUser gitLabUser, String personalAccessToken, GitLabClient gitLabClient, List<GitLabGroup> groupsFromGitLabForAUser, Map<String, List<String>> groupsFromRole) throws IOException {
        final List<GitLabGroup> matchingGroups = filterGroupBasedOnRoleConfiguration(groupsFromGitLabForAUser, groupsFromRole);

        for (GitLabGroup gitLabGroup : matchingGroups) {
            final List<String> accessLevels = groupsFromRole.get(gitLabGroup.getName());

            if (accessLevels == null || accessLevels.isEmpty()) {
                LOG.info(format("User `{0}` is member of `{1}` group.", gitLabUser.getUsername(), gitLabGroup.getName()));
                return true;
            }

            final MembershipInfo membershipInfo = gitLabClient.groupMembershipInfo(personalAccessToken, gitLabGroup.getId(), gitLabUser.getId());

            if (membershipInfo.getAccessLevel() != null && accessLevels.contains(membershipInfo.getAccessLevel().toString().toLowerCase())) {
                LOG.info(format("User `{0}` is member of `{1}` group with access level `{2}`.", gitLabUser.getUsername(), gitLabGroup.getName(), membershipInfo.getAccessLevel()));
                return true;
            }
        }
        return false;
    }

    private List<GitLabGroup> filterGroupBasedOnRoleConfiguration(List<GitLabGroup> groupsFromGitLab, Map<String, List<String>> groupsFromRole) throws IOException {
        final List<GitLabGroup> gitLabGroups = new ArrayList<>();
        for (GitLabGroup groupFromGitLab : groupsFromGitLab) {
            if (groupsFromRole.containsKey(groupFromGitLab.getName())) {
                gitLabGroups.add(groupFromGitLab);
            }
        }
        return gitLabGroups;
    }
}
