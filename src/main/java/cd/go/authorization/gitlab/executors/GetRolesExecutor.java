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

package cd.go.authorization.gitlab.executors;

import cd.go.authorization.gitlab.GitLabAuthorizer;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.requests.GetRolesRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.List;

import static cd.go.authorization.gitlab.GitLabPlugin.LOG;
import static cd.go.authorization.gitlab.utils.Util.GSON;
import static java.lang.String.format;

public class GetRolesExecutor implements RequestExecutor {
    private final GetRolesRequest request;
    private final GitLabAuthorizer gitLabAuthorizer;

    public GetRolesExecutor(GetRolesRequest request) {
        this(request, new GitLabAuthorizer());
    }

    GetRolesExecutor(GetRolesRequest request, GitLabAuthorizer gitLabAuthorizer) {
        this.request = request;
        this.gitLabAuthorizer = gitLabAuthorizer;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        if (request.getRoles().isEmpty()) {
            LOG.debug("[Get User Roles] Server sent empty roles config. Nothing to do!.");
            return DefaultGoPluginApiResponse.success("[]");
        }

        GitLabUser user = request.getAuthConfig().gitLabConfiguration().gitLabClient().user(request.getAuthConfig().gitLabConfiguration().personalAccessToken());

        if (user == null) {
            LOG.error(format("[Get User Roles] User %s does not exist in GitLab.", request.getUsername()));
            return DefaultGoPluginApiResponse.error("");
        }

        List<String> roles = gitLabAuthorizer.authorize(user, request.getAuthConfig(), request.getRoles());

        LOG.debug(format("[Get User Roles] User %s has %s roles.", request.getUsername(), roles));
        return DefaultGoPluginApiResponse.success(GSON.toJson(roles));
    }
}
