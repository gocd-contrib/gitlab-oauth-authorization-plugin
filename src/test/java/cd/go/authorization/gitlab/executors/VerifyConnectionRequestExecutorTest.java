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

import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.requests.VerifyConnectionRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerifyConnectionRequestExecutorTest {
    private VerifyConnectionRequest request;
    private VerifyConnectionRequestExecutor executor;

    @BeforeEach
    public void setup() throws Exception {
        request = mock(VerifyConnectionRequest.class);

        executor = new VerifyConnectionRequestExecutor(request);
    }

    @Test
    public void shouldReturnValidationFailedStatusForInvalidAuthConfig() throws Exception {
        when(request.gitLabConfiguration()).thenReturn(new GitLabConfiguration());

        GoPluginApiResponse response = executor.execute();

        String expectedJSON = "{\n" +
                "  \"message\": \"Validation failed for the given Auth Config\",\n" +
                "  \"errors\": [\n" +
                "    {\n" +
                "      \"key\": \"ApplicationId\",\n" +
                "      \"message\": \"ApplicationId must not be blank.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"ClientSecret\",\n" +
                "      \"message\": \"ClientSecret must not be blank.\"\n" +
                "    },\n" +
                "    {\n" +
                "       \"key\":\"PersonalAccessToken\",\n" +
                "       \"message\":\"PersonalAccessToken must not be blank.\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"validation-failed\"\n" +
                "}";

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldReturnSuccessResponseOnSuccessfulVerification() throws Exception {
        final GitLabConfiguration gitLabConfiguration = mock(GitLabConfiguration.class);

        when(request.gitLabConfiguration()).thenReturn(gitLabConfiguration);

        GoPluginApiResponse response = executor.execute();

        String expectedJSON = "{\n" +
                "  \"message\": \"Connection ok\",\n" +
                "  \"status\": \"success\"\n" +
                "}";

        assertThat(response.responseCode()).isEqualTo(200);
        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }
}