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

import cd.go.authorization.gitlab.executors.ValidateUserRequestExecutor;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidateUserRequestTest {
    private GoPluginApiRequest apiRequest;

    @BeforeEach
    public void setUp() {
        apiRequest = mock(GoPluginApiRequest.class);

        when(apiRequest.requestBody()).thenReturn("{\n" +
                "  \"auth_config\": {\n" +
                "    \"configuration\": {\n" +
                "       \"ApplicationId\": \"client-id\",\n" +
                "       \"ClientSecret\": \"client-secret\",\n" +
                "       \"AuthenticateWith\": \"GitLabEnterprise\",\n" +
                "       \"GitLabEnterpriseUrl\": \"my-enterprise-url\",\n" +
                "       \"PersonalAccessToken\": \"Baz\"\n" +
                "    },\n" +
                "    \"id\": \"GitLab\"\n" +
                "  },\n" +
                "  \"username\": \"bob\"\n" +
                "}");
    }

    @Test
    public void shouldParseRequest() {
        ValidateUserRequest request = (ValidateUserRequest) ValidateUserRequest.from(apiRequest);

        assertThat(request.getUsername()).isEqualTo("bob");
        assertThat(request.getAuthConfig().getId()).isEqualTo("GitLab");
    }

    @Test
    public void shouldReturnValidExecutor() {
        Request request = ValidateUserRequest.from(apiRequest);

        assertThat(request.executor() instanceof ValidateUserRequestExecutor).isEqualTo(true);
    }
}