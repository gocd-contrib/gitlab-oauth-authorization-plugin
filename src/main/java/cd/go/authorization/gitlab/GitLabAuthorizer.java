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
import cd.go.authorization.gitlab.models.AuthConfig;
import cd.go.authorization.gitlab.models.GitLabRole;
import cd.go.authorization.gitlab.models.TokenInfo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cd.go.authorization.gitlab.GitLabPlugin.LOG;
import static java.util.stream.Collectors.toList;

public class GitLabAuthorizer {
    private final GroupMembershipChecker groupMembershipChecker;
    private final ProjectMembershipChecker projectMembershipChecker;

    public GitLabAuthorizer() {
        this(new GroupMembershipChecker(), new ProjectMembershipChecker());
    }

    GitLabAuthorizer(GroupMembershipChecker groupMembershipChecker, ProjectMembershipChecker projectMembershipChecker) {
        this.groupMembershipChecker = groupMembershipChecker;
        this.projectMembershipChecker = projectMembershipChecker;
    }

    public List<String> authorize(GitLabUser gitLabUser, TokenInfo tokenInfo, AuthConfig authConfig, List<GitLabRole> roles) throws IOException {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> assignedRoles = checkIfUserIsWhiteListed(gitLabUser.getUsername(), roles);

        final List<GitLabRole> remainingRoles = roles.stream().filter(role -> !assignedRoles.contains(role.name())).collect(toList());

        if (remainingRoles.isEmpty()) {
            LOG.debug("No more roles to check.");
            return assignedRoles;
        }

        final GitLabClient gitLabClient = authConfig.gitLabConfiguration().gitLabClient();
        final List<GitLabGroup> groupsFromGitLab = gitLabClient.groups(tokenInfo);
        final List<GitLabProject> projectsFromGitLab = gitLabClient.projects(tokenInfo);

        for (GitLabRole role : remainingRoles) {
            final Map<String, List<String>> groupsFromRole = role.roleConfiguration().groups();

            if (groupMembershipChecker.memberOfAtLeastOneGroup(gitLabUser, tokenInfo, gitLabClient, groupsFromGitLab, groupsFromRole)) {
                assignedRoles.add(role.name());
                continue;
            }

            final Map<String, List<String>> projectsFromRole = role.roleConfiguration().projects();

            if (projectMembershipChecker.memberOfAtLeastOneProject(gitLabUser, tokenInfo, gitLabClient, projectsFromGitLab, projectsFromRole)) {
                assignedRoles.add(role.name());
            }
        }

        return assignedRoles;
    }

    private List<String> checkIfUserIsWhiteListed(String username, List<GitLabRole> roles) {
        return roles.stream().filter(role -> role.roleConfiguration().users().contains(username))
                .map(role -> role.name())
                .collect(toList());
    }
}
