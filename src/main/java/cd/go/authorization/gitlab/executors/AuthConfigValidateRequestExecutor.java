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


import cd.go.authorization.gitlab.annotation.MetadataValidator;
import cd.go.authorization.gitlab.annotation.ValidationResult;
import cd.go.authorization.gitlab.models.AuthenticateWith;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.requests.AuthConfigValidateRequest;
import cd.go.authorization.gitlab.utils.Util;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;


public class AuthConfigValidateRequestExecutor implements RequestExecutor {
    private final AuthConfigValidateRequest request;

    public AuthConfigValidateRequestExecutor(AuthConfigValidateRequest request) {
        this.request = request;
    }

    public GoPluginApiResponse execute() throws Exception {
        final GitLabConfiguration gitLabConfiguration = request.gitLabConfiguration();
        final ValidationResult validationResult = new MetadataValidator().validate(gitLabConfiguration);

        if (gitLabConfiguration.authenticateWith() == AuthenticateWith.GITLAB_ENTERPRISE && Util.isBlank(gitLabConfiguration.gitLabEnterpriseUrl())) {
            validationResult.addError("GitLabEnterpriseUrl", "GitLabEnterpriseUrl must not be blank.");
        }

        return DefaultGoPluginApiResponse.success(validationResult.toJSON());
    }
}
