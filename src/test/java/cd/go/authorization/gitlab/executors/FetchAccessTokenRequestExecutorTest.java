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
import cd.go.authorization.gitlab.client.GitLabClient;
import cd.go.authorization.gitlab.exceptions.AuthenticationException;
import cd.go.authorization.gitlab.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.gitlab.models.AuthConfig;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.models.TokenInfo;
import cd.go.authorization.gitlab.requests.FetchAccessTokenRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FetchAccessTokenRequestExecutorTest {
    private FetchAccessTokenRequest fetchAccessTokenRequest;
    private AuthConfig authConfig;
    private GitLabConfiguration gitLabConfiguration;
    private FetchAccessTokenRequestExecutor executor;

    @BeforeEach
    public void setUp() throws Exception {
        fetchAccessTokenRequest = mock(FetchAccessTokenRequest.class);
        authConfig = mock(AuthConfig.class);
        gitLabConfiguration = mock(GitLabConfiguration.class);

        when(authConfig.gitLabConfiguration()).thenReturn(gitLabConfiguration);

        executor = new FetchAccessTokenRequestExecutor(fetchAccessTokenRequest);
    }

    @Test
    public void shouldErrorOutIfAuthConfigIsNotProvided() throws Exception {
        final GoPluginApiRequest request = mock(GoPluginApiRequest.class);
        when(request.requestBody()).thenReturn("{\"auth_configs\":[]}");

        FetchAccessTokenRequestExecutor executor = new FetchAccessTokenRequestExecutor(FetchAccessTokenRequest.from(request));

        assertThatThrownBy(executor::execute).isInstanceOf(NoAuthorizationConfigurationException.class);

        verify(fetchAccessTokenRequest, never()).validateState();
    }

    @Test
    public void shouldFetchAccessToken() throws Exception {
        final GitLabClient gitLabClient = mock(GitLabClient.class);

        when(fetchAccessTokenRequest.authConfigs()).thenReturn(Collections.singletonList(authConfig));
        when(fetchAccessTokenRequest.requestParameters()).thenReturn(Collections.singletonMap("code", "code-received-in-previous-step"));
        when(authConfig.gitLabConfiguration()).thenReturn(gitLabConfiguration);
        when(gitLabConfiguration.gitLabClient()).thenReturn(gitLabClient);
        when(fetchAccessTokenRequest.authSession()).thenReturn(Collections.singletonMap(Constants.AUTH_CODE_VERIFIER, "code-verifier"));
        when(gitLabClient.fetchAccessToken("code-received-in-previous-step", "code-verifier")).thenReturn(new TokenInfo("token-444248275346-5758603453985735", "bearer", 7200, "refresh-token"));

        final GoPluginApiResponse response = executor.execute();

        String expectedJSON = "{\n" +
                "  \"access_token\": \"token-444248275346-5758603453985735\",\n" +
                "  \"token_type\": \"bearer\",\n" +
                "  \"expires_in\": 7200,\n" +
                "  \"refresh_token\": \"refresh-token\"\n" +
                "}";

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), true);

        verify(fetchAccessTokenRequest).validateState();
    }


    @Test
    public void fetchAccessToken_shouldErrorIfStateDoesNotMatch() {
        when(fetchAccessTokenRequest.authConfigs()).thenReturn(Collections.singletonList(authConfig));
        when(fetchAccessTokenRequest.authSession()).thenReturn(Map.of(Constants.AUTH_SESSION_STATE, "some-value"));
        when(fetchAccessTokenRequest.requestParameters()).thenReturn(Collections.singletonMap("code", "code-received-in-previous-step"));
        doThrow(new AuthenticationException("error validating state")).when(fetchAccessTokenRequest).validateState();

        Exception exception = assertThrows(AuthenticationException.class, executor::execute);
        assertThat(exception.getMessage()).isEqualTo("error validating state");
    }
}