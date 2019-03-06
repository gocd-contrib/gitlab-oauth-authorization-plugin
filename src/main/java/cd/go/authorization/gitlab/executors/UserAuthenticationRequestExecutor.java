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

package cd.go.authorization.gitlab.executors;

import cd.go.authorization.gitlab.GitLabAuthenticator;
import cd.go.authorization.gitlab.GitLabAuthorizer;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.gitlab.models.AuthConfig;
import cd.go.authorization.gitlab.models.User;
import cd.go.authorization.gitlab.requests.UserAuthenticationRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

import static cd.go.authorization.gitlab.utils.Util.GSON;
import static com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;

public class UserAuthenticationRequestExecutor implements RequestExecutor {
    private final UserAuthenticationRequest request;
    private final GitLabAuthenticator gitLabAuthenticator;
    private final GitLabAuthorizer gitLabAuthorizer;

    public UserAuthenticationRequestExecutor(UserAuthenticationRequest request) {
        this(request, new GitLabAuthenticator(), new GitLabAuthorizer());
    }

    UserAuthenticationRequestExecutor(UserAuthenticationRequest request, GitLabAuthenticator gitLabAuthenticator, GitLabAuthorizer gitLabAuthorizer) {
        this.request = request;
        this.gitLabAuthenticator = gitLabAuthenticator;
        this.gitLabAuthorizer = gitLabAuthorizer;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        if (request.authConfigs() == null || request.authConfigs().isEmpty()) {
            throw new NoAuthorizationConfigurationException("[Authenticate] No authorization configuration found.");
        }

        final AuthConfig authConfig = request.authConfigs().get(0);
        final GitLabUser gitLabUser = gitLabAuthenticator.authenticate(request.tokenInfo(), authConfig);

        Map<String, Object> userMap = new HashMap<>();
        if (gitLabUser != null) {
            userMap.put("user", new User(gitLabUser));
            userMap.put("roles", gitLabAuthorizer.authorize(gitLabUser, authConfig, request.roles()));
        }

        DefaultGoPluginApiResponse response = new DefaultGoPluginApiResponse(SUCCESS_RESPONSE_CODE, GSON.toJson(userMap));
        return response;
    }
}
