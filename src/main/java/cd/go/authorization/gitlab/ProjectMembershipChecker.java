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
import cd.go.authorization.gitlab.client.models.GitLabProject;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.client.models.MembershipInfo;
import cd.go.authorization.gitlab.models.TokenInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cd.go.authorization.gitlab.GitLabPlugin.LOG;
import static java.text.MessageFormat.format;

public class ProjectMembershipChecker {

    public boolean memberOfAtLeastOneProject(GitLabUser gitLabUser, String personalAccessToken, GitLabClient gitLabClient, List<GitLabProject> projectsFromGitLabForAUser, Map<String, List<String>> projectsFromRole) throws IOException {
        final List<GitLabProject> matchingProjects = filterGroupBasedOnRoleConfiguration(projectsFromGitLabForAUser, projectsFromRole);

        for (GitLabProject gitLabProject : matchingProjects) {
            final List<String> accessLevels = projectsFromRole.get(gitLabProject.getName());

            if (accessLevels == null || accessLevels.isEmpty()) {
                LOG.info(format("User `{0}` is member of `{1}` project.", gitLabUser.getUsername(), gitLabProject.getName()));
                return true;
            }

            final MembershipInfo membershipInfo = gitLabClient.projectMembershipInfo(personalAccessToken, gitLabProject.getId(), gitLabUser.getId());

            if (membershipInfo.getAccessLevel() != null && accessLevels.contains(membershipInfo.getAccessLevel().toString().toLowerCase())) {
                LOG.info(format("User `{0}` is member of `{1}` project with access level `{2}`.", gitLabUser.getUsername(), gitLabProject.getName(), membershipInfo.getAccessLevel()));
                return true;
            }
        }
        return false;
    }

    private List<GitLabProject> filterGroupBasedOnRoleConfiguration(List<GitLabProject> projectsFromGitLab, Map<String, List<String>> projectsFromRole) throws IOException {
        final List<GitLabProject> gitLabProjects = new ArrayList<>();
        for (GitLabProject projectFromGitLab : projectsFromGitLab) {
            if (projectsFromRole.containsKey(projectFromGitLab.getName())) {
                gitLabProjects.add(projectFromGitLab);
            }
        }
        return gitLabProjects;
    }
}
