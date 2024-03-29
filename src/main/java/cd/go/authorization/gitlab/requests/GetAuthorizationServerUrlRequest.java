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

import cd.go.authorization.gitlab.CallbackURL;
import cd.go.authorization.gitlab.executors.GetAuthorizationServerUrlRequestExecutor;
import cd.go.authorization.gitlab.models.AuthConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

import java.util.List;

public class GetAuthorizationServerUrlRequest extends Request {
    @Expose
    @SerializedName("authorization_server_callback_url")
    private String callbackUrl;

    @Expose
    @SerializedName("auth_configs")
    private List<AuthConfig> authConfigs;

    public static GetAuthorizationServerUrlRequest from(GoPluginApiRequest apiRequest) {
        final GetAuthorizationServerUrlRequest authorizationServerUrlRequest = Request.from(apiRequest, GetAuthorizationServerUrlRequest.class);
        CallbackURL.instance().updateRedirectURL(authorizationServerUrlRequest.callbackUrl());
        return authorizationServerUrlRequest;
    }

    public String callbackUrl() {
        return callbackUrl;
    }

    public List<AuthConfig> authConfigs() {
        return authConfigs;
    }

    @Override
    public GetAuthorizationServerUrlRequestExecutor executor() {
        return new GetAuthorizationServerUrlRequestExecutor(this);
    }
}
