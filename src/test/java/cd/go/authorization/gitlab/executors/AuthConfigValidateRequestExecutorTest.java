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

import cd.go.authorization.gitlab.requests.AuthConfigValidateRequest;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthConfigValidateRequestExecutorTest {

    private GoPluginApiRequest request;

    @BeforeEach
    public void setup() throws Exception {
        request = mock(GoPluginApiRequest.class);
    }

    @Test
    public void shouldValidateMandatoryKeys() throws Exception {
        when(request.requestBody()).thenReturn(new Gson().toJson(singletonMap("AuthorizeUsing", "UserAccessToken")));

        GoPluginApiResponse response = AuthConfigValidateRequest.from(request).execute();
        String json = response.responseBody();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"ApplicationId\",\n" +
                "    \"message\": \"ApplicationId must not be blank.\"\n" +
                "  },\n" +
                "  {" +
                "   \"key\":\"PersonalAccessToken\",\n" +
                "   \"message\":\"PersonalAccessToken must not be blank.\"\n" +
                "   }," +
                "  {\n" +
                "    \"key\": \"ClientSecret\",\n" +
                "    \"message\": \"ClientSecret must not be blank.\"\n" +
                "  }\n" +
                "]";

        JSONAssert.assertEquals(expectedJSON, json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateGitLabEnterpriseUrl() throws Exception {
        when(request.requestBody()).thenReturn("{\n" +
                "  \"ApplicationId\": \"client-id\",\n" +
                "  \"AuthenticateWith\": \"GitLabEnterprise\",\n" +
                "  \"ClientSecret\": \"client-secret\",\n" +
                "  \"PersonalAccessToken\":\"some-random-token\"" +
                "}");

        GoPluginApiResponse response = AuthConfigValidateRequest.from(request).execute();

        String expectedJSON = "[\n" +
                "  {\n" +
                "    \"key\": \"GitLabEnterpriseUrl\",\n" +
                "    \"message\": \"GitLabEnterpriseUrl must not be blank.\"\n" +
                "  }\n" +
                "]";

        JSONAssert.assertEquals(expectedJSON, response.responseBody(), JSONCompareMode.NON_EXTENSIBLE);
    }
}