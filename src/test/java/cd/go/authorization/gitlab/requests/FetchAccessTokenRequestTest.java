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
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class FetchAccessTokenRequestTest {
    @Mock
    private GoPluginApiRequest apiRequest;

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
        when(apiRequest.requestParameters()).thenReturn(Map.of("code", "authorization-code"));
        when(apiRequest.requestBody()).thenReturn("""
                {
                  "auth_configs": [
                    {
                      "id": "gitlab-auth-config",
                      "configuration": {
                        "GoServerUrl": "https://your.go.server.url",
                        "ApplicationId": "client-id",
                        "ClientSecret": "client-secret"
                      }
                    }
                  ],
                  "auth_session": {
                    "oauth2_code_verifier_encoded": "code-verifier"
                  }
                }
                """);
    }

    @Test
    public void shouldDeserializeGoPluginApiRequestToFetchAccessTokenRequest() throws Exception {
        final FetchAccessTokenRequest request = FetchAccessTokenRequest.from(apiRequest);

        assertThat(request.authConfigs()).hasSize(1);
        assertThat(request.executor()).isInstanceOf(FetchAccessTokenRequestExecutor.class);

        final AuthConfig authConfig = request.authConfigs().get(0);
        assertThat(authConfig.getId()).isEqualTo("gitlab-auth-config");
        assertThat(authConfig.gitLabConfiguration().applicationId()).isEqualTo("client-id");
        assertThat(authConfig.gitLabConfiguration().clientSecret()).isEqualTo("client-secret");

        assertThat(request.authSession()).containsOnly(Map.entry("oauth2_code_verifier_encoded", "code-verifier"));
    }

    @Test
    public void shouldValidateAuthorizationCode() {
        assertThat(FetchAccessTokenRequest.from(apiRequest).authorizationCode())
                .isEqualTo("authorization-code");
    }

    @Test
    public void shouldFailIfAuthorizationNotFound() {
        when(apiRequest.requestParameters()).thenReturn(Collections.emptyMap());
        assertThatThrownBy(() -> FetchAccessTokenRequest.from(apiRequest).authorizationCode())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[Get Access Token] Expecting `code` in request params, but not received.");
    }

    @Test
    public void shouldValidateCodeVerifier() {
        assertThat(FetchAccessTokenRequest.from(apiRequest).codeVerifierEncoded())
                .isEqualTo("code-verifier");
    }

    @Test
    public void shouldFailIfCodeVerifierNotFound() {
        when(apiRequest.requestBody()).thenReturn("""
                {
                  "auth_configs": [
                    {
                      "id": "gitlab-auth-config",
                      "configuration": {
                        "GoServerUrl": "https://your.go.server.url",
                        "ApplicationId": "client-id",
                        "ClientSecret": "client-secret"
                      }
                    }
                  ],
                  "auth_session": {}
                }
                """);
        assertThatThrownBy(() -> FetchAccessTokenRequest.from(apiRequest).codeVerifierEncoded())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[Get Access Token] OAuth2 code verifier is missing from session");
    }
}