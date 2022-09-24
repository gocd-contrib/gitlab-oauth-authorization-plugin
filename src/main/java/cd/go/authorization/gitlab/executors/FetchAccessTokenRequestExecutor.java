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

import cd.go.authorization.gitlab.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.gitlab.models.AuthConfig;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.models.TokenInfo;
import cd.go.authorization.gitlab.requests.FetchAccessTokenRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import okhttp3.OkHttpClient;

public class FetchAccessTokenRequestExecutor implements RequestExecutor {
    private final FetchAccessTokenRequest request;
    private final OkHttpClient httpClient;

    public FetchAccessTokenRequestExecutor(FetchAccessTokenRequest request) {
        this(request, new OkHttpClient());
    }

    FetchAccessTokenRequestExecutor(FetchAccessTokenRequest request, OkHttpClient httpClient) {
        this.request = request;
        this.httpClient = httpClient;
    }

    public GoPluginApiResponse execute() throws Exception {
        if (request.authConfigs() == null || request.authConfigs().isEmpty()) {
            throw new NoAuthorizationConfigurationException("[Get Access Token] No authorization configuration found.");
        }

        if (!request.requestParameters().containsKey("code")) {
            throw new IllegalArgumentException("Get Access Token] Expecting `code` in request params, but not received.");
        }

        final AuthConfig authConfig = request.authConfigs().get(0);
        final GitLabConfiguration gitLabConfiguration = authConfig.gitLabConfiguration();

        final TokenInfo tokenInfo = gitLabConfiguration.gitLabClient().fetchAccessToken(request.requestParameters().get("code"));

        return DefaultGoPluginApiResponse.success(tokenInfo.toJSON());
    }
}
