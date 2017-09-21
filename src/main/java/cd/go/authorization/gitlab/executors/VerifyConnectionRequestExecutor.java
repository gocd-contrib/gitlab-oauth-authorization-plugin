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

import cd.go.authorization.gitlab.annotation.MetadataValidator;
import cd.go.authorization.gitlab.annotation.ValidationResult;
import cd.go.authorization.gitlab.requests.VerifyConnectionRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;

import static cd.go.authorization.gitlab.utils.Util.GSON;

public class VerifyConnectionRequestExecutor implements RequestExecutor {
    private final VerifyConnectionRequest request;

    public VerifyConnectionRequestExecutor(VerifyConnectionRequest request) {
        this.request = request;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        final ValidationResult validationResult = validate();
        if (validationResult.hasErrors()) {
            return validationFailureResponse(validationResult);
        }

        return successResponse();
    }

    private ValidationResult validate() {
        return new MetadataValidator().validate(request.gitLabConfiguration());
    }

    private GoPluginApiResponse successResponse() {
        return responseWith("success", "Connection ok", null);
    }

    private GoPluginApiResponse validationFailureResponse(ValidationResult errors) {
        return responseWith("validation-failed", "Validation failed for the given Auth Config", errors);
    }

    private GoPluginApiResponse responseWith(String status, String message, ValidationResult result) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);

        if (result != null && result.hasErrors()) {
            response.put("errors", result.errors());
        }

        return DefaultGoPluginApiResponse.success(GSON.toJson(response));
    }
}
