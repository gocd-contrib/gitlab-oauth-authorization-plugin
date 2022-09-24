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

import cd.go.authorization.gitlab.GitLabAuthenticator;
import cd.go.authorization.gitlab.GitLabAuthorizer;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.gitlab.models.AuthConfig;
import cd.go.authorization.gitlab.models.TokenInfo;
import cd.go.authorization.gitlab.requests.UserAuthenticationRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAuthenticationRequestExecutorTest {
    private UserAuthenticationRequest request;
    private AuthConfig authConfig;

    private UserAuthenticationRequestExecutor executor;
    private GitLabAuthenticator authenticator;
    private GitLabAuthorizer authorizer;

    @BeforeEach
    public void setUp() throws Exception {
        request = mock(UserAuthenticationRequest.class);
        authConfig = mock(AuthConfig.class);
        authenticator = mock(GitLabAuthenticator.class);
        authorizer = mock(GitLabAuthorizer.class);

        executor = new UserAuthenticationRequestExecutor(request, authenticator, authorizer);
    }

    @Test
    public void shouldErrorOutIfAuthConfigIsNotProvided() throws Exception {
        when(request.authConfigs()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> executor.execute())
                .isInstanceOf(NoAuthorizationConfigurationException.class)
                .hasMessage("[Authenticate] No authorization configuration found.");

    }

    @Test
    public void shouldAuthenticateUser() throws Exception {
        final GitLabUser gitLabUser = mock(GitLabUser.class);
        final TokenInfo tokenInfo = new TokenInfo("access-token", "token-type", 7200, "refresh-token");

        when(request.authConfigs()).thenReturn(Collections.singletonList(authConfig));
        when(request.tokenInfo()).thenReturn(tokenInfo);
        when(authenticator.authenticate(tokenInfo, authConfig)).thenReturn(gitLabUser);
        when(gitLabUser.getEmail()).thenReturn("bford@example.com");
        when(gitLabUser.getName()).thenReturn("Bob");
        when(gitLabUser.getUsername()).thenReturn("bford");

        final GoPluginApiResponse response = executor.execute();

        String expectedJSON = "{\n" +
                "  \"roles\": [],\n" +
                "  \"user\": {\n" +
                "    \"username\": \"bford\",\n" +
                "    \"display_name\": \"Bob\",\n" +
                "    \"email\": \"bford@example.com\"\n" +
                "  }\n" +
                "}";

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);
    }
}