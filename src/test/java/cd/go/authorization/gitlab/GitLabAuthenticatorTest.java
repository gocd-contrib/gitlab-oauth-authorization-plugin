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

package cd.go.authorization.gitlab;

import cd.go.authorization.gitlab.client.GitLabClient;
import cd.go.authorization.gitlab.client.models.GitLabUser;
import cd.go.authorization.gitlab.models.AuthConfig;
import cd.go.authorization.gitlab.models.GitLabConfiguration;
import cd.go.authorization.gitlab.models.TokenInfo;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitLabAuthenticatorTest {

    private GitLabAuthenticator authenticator;
    private AuthConfig authConfig;
    private GitLabConfiguration gitLabConfiguration;
    private TokenInfo tokenInfo;
    private GitLabClient gitLabClient;

    @Before
    public void setUp() throws Exception {
        authConfig = mock(AuthConfig.class);
        gitLabConfiguration = mock(GitLabConfiguration.class);
        tokenInfo = mock(TokenInfo.class);
        gitLabClient = mock(GitLabClient.class);

        when(authConfig.gitLabConfiguration()).thenReturn(gitLabConfiguration);
        when(gitLabConfiguration.gitLabClient()).thenReturn(gitLabClient);

        authenticator = new GitLabAuthenticator();
    }

    @Test
    public void shouldAuthenticateUser() throws Exception {
        when(gitLabClient.user(tokenInfo)).thenReturn(new GitLabUser("username", "DisplayName", "email"));

        final GitLabUser user = authenticator.authenticate(tokenInfo, authConfig);

        assertThat(user, is(new GitLabUser("username", "DisplayName", "email")));
    }
}