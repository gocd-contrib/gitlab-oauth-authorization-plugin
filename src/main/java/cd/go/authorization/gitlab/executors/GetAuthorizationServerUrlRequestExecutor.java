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

import cd.go.authorization.gitlab.Constants;
import cd.go.authorization.gitlab.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.requests.GetAuthorizationServerUrlRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.List;
import java.util.Map;

import static cd.go.authorization.gitlab.GitLabPlugin.LOG;
import static cd.go.authorization.gitlab.utils.Util.GSON;

public class GetAuthorizationServerUrlRequestExecutor implements RequestExecutor {
    private final GetAuthorizationServerUrlRequest request;

    public GetAuthorizationServerUrlRequestExecutor(GetAuthorizationServerUrlRequest request) {
        this.request = request;
    }

    public GoPluginApiResponse execute() throws Exception {
        if (request.authConfigs() == null || request.authConfigs().isEmpty()) {
            throw new NoAuthorizationConfigurationException("[Authorization Server Url] No authorization configuration found.");
        }

        LOG.debug("[Get Authorization Server URL] Getting authorization server url from auth config.");

        final GitLabConfiguration gitLabConfiguration = request.authConfigs().get(0).gitLabConfiguration();

        List<String> args = gitLabConfiguration.gitLabClient().authorizationServerArgs(request.callbackUrl());

        return DefaultGoPluginApiResponse.success(GSON.toJson(Map.of(
                "authorization_server_url", args.get(0),
                "auth_session", Map.of(Constants.AUTH_SESSION_STATE, args.get(1))
        )));
    }
}
