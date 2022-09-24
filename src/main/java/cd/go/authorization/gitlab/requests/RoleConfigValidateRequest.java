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

package cd.go.authorization.gitlab.requests;

import cd.go.authorization.gitlab.executors.RequestExecutor;
import cd.go.authorization.gitlab.executors.RoleConfigValidateRequestExecutor;
import cd.go.authorization.gitlab.models.GitLabRoleConfiguration;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

public class RoleConfigValidateRequest extends Request {
    private final GitLabRoleConfiguration gitLabRoleConfiguration;

    public RoleConfigValidateRequest(GitLabRoleConfiguration gitLabRoleConfiguration) {
        this.gitLabRoleConfiguration = gitLabRoleConfiguration;
    }

    @Override
    public RequestExecutor executor() {
        return new RoleConfigValidateRequestExecutor(this);
    }

    public GitLabRoleConfiguration gitLabRoleConfiguration() {
        return gitLabRoleConfiguration;
    }

    public static final RoleConfigValidateRequest from(GoPluginApiRequest apiRequest) {
        return new RoleConfigValidateRequest(GitLabRoleConfiguration.fromJSON(apiRequest.requestBody()));
    }

}
