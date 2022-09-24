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

import cd.go.authorization.gitlab.executors.UserAuthenticationRequestExecutor;
import cd.go.authorization.gitlab.models.AuthConfig;
import cd.go.authorization.gitlab.models.TokenInfo;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserAuthenticationRequestTest {
    @Mock
    private GoPluginApiRequest request;

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
    }

    @Test
    public void shouldDeserializeGoPluginApiRequestToUserAuthenticationRequest() throws Exception {
        String responseBody = "{\n" +
                "  \"credentials\": {\n" +
                "    \"access_token\": \"access-token\",\n" +
                "    \"token_type\": \"token\",\n" +
                "    \"refresh_token\": \"refresh-token\",\n" +
                "    \"expires_in\": \"7200\"\n" +
                "  },\n" +
                "  \"auth_configs\": [\n" +
                "    {\n" +
                "      \"id\": \"gitlab-config\",\n" +
                "      \"configuration\": {\n" +
                "        \"AuthenticateWith\": \"GitLab\",\n" +
                "        \"ApplicationId\": \"client-id\",\n" +
                "        \"ClientSecret\": \"client-secret\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]" +
                "}";

        when(request.requestBody()).thenReturn(responseBody);

        final UserAuthenticationRequest request = UserAuthenticationRequest.from(this.request);

        assertThat(request.authConfigs()).hasSize(1);
        assertThat(request.executor()).isInstanceOf(UserAuthenticationRequestExecutor.class);

        assertAuthConfig(request.authConfigs().get(0));
        assertTokenInfo(request.tokenInfo());
    }

    private void assertTokenInfo(TokenInfo tokenInfo) {
        assertThat(tokenInfo.accessToken()).isEqualTo("access-token");
        assertThat(tokenInfo.tokenType()).isEqualTo("token");
        assertThat(tokenInfo.expiresIn()).isEqualTo(7200);
        assertThat(tokenInfo.refreshToken()).isEqualTo("refresh-token");
    }

    private void assertAuthConfig(AuthConfig authConfig) {
        assertThat(authConfig.getId()).isEqualTo("gitlab-config");
        assertThat(authConfig.gitLabConfiguration().applicationId()).isEqualTo("client-id");
        assertThat(authConfig.gitLabConfiguration().clientSecret()).isEqualTo("client-secret");
    }
}