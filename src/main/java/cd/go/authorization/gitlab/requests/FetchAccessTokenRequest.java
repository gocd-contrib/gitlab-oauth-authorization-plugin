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

import cd.go.authorization.gitlab.executors.FetchAccessTokenRequestExecutor;
import cd.go.authorization.gitlab.models.AuthConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import java.util.List;

public class FetchAccessTokenRequest extends Request {
    @Expose
    @SerializedName("auth_configs")
    private List<AuthConfig> authConfigs;

    public static FetchAccessTokenRequest from(GoPluginApiRequest apiRequest) {
        return Request.from(apiRequest, FetchAccessTokenRequest.class);
    }

    public List<AuthConfig> authConfigs() {
        return authConfigs;
    }

    @Override
    public FetchAccessTokenRequestExecutor executor() {
        return new FetchAccessTokenRequestExecutor(this);
    }
}
