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

package cd.go.authorization.gitlab.models;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GitLabConfigurationTest {

    @Test
    public void shouldDeserializeGitLabConfiguration() throws Exception {
        final GitLabConfiguration gitLabConfiguration = GitLabConfiguration.fromJSON("{\n" +
                "  \"ApplicationId\": \"client-id\",\n" +
                "  \"AuthenticateWith\": \"GitLabEnterprise\",\n" +
                "  \"GitLabEnterpriseUrl\": \"https://enterprise.url\",\n" +
                "  \"ClientSecret\": \"client-secret\"" +
                "}");

        assertThat(gitLabConfiguration.applicationId()).isEqualTo("client-id");
        assertThat(gitLabConfiguration.clientSecret()).isEqualTo("client-secret");
        assertThat(gitLabConfiguration.gitLabEnterpriseUrl()).isEqualTo("https://enterprise.url");
        assertThat(gitLabConfiguration.authenticateWith()).isEqualTo(AuthenticateWith.GITLAB_ENTERPRISE);
    }

    @Test
    public void shouldSerializeToJSON() throws Exception {
        GitLabConfiguration gitLabConfiguration = new GitLabConfiguration("client-id", "client-secret",
                AuthenticateWith.GITLAB_ENTERPRISE, "http://enterprise.url", "some-random-token");

        String expectedJSON = "{\n" +
                "  \"ApplicationId\": \"client-id\",\n" +
                "  \"ClientSecret\": \"client-secret\",\n" +
                "  \"AuthenticateWith\": \"GitLabEnterprise\",\n" +
                "  \"GitLabEnterpriseUrl\": \"http://enterprise.url\",\n" +
                "  \"PersonalAccessToken\":\"some-random-token\"" +
                "}";

        JSONAssert.assertEquals(expectedJSON, gitLabConfiguration.toJSON(), true);

    }

    @Test
    public void shouldConvertConfigurationToProperties() throws Exception {
        GitLabConfiguration gitLabConfiguration = new GitLabConfiguration("client-id", "client-secret", AuthenticateWith.GITLAB_ENTERPRISE, "http://enterprise.url", "some-random-token");

        final Map<String, String> properties = gitLabConfiguration.toProperties();

        assertThat(properties).containsEntry("ApplicationId", "client-id");
        assertThat(properties).containsEntry("ClientSecret", "client-secret");
        assertThat(properties).containsEntry("AuthenticateWith", "GitLabEnterprise");
        assertThat(properties).containsEntry("GitLabEnterpriseUrl", "http://enterprise.url");
    }
}