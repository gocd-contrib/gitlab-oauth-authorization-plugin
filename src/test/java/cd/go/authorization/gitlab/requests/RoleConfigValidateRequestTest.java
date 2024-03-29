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

import cd.go.authorization.gitlab.models.GitLabRoleConfiguration;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class RoleConfigValidateRequestTest {

    @Mock
    private GoPluginApiRequest apiRequest;

    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);
    }

    @Test
    public void shouldDeserializeGoPluginApiRequestToRoleConfigValidateRequest() throws Exception {
        String responseBody = "{\n" +
                "  \"Groups\": \"group-1: guest, owner\",\n" +
                "  \"Projects\": \"project-1:developer\"" +
                "}";

        when(apiRequest.requestBody()).thenReturn(responseBody);

        final RoleConfigValidateRequest request = RoleConfigValidateRequest.from(apiRequest);
        final GitLabRoleConfiguration gitLabRoleConfiguration = request.gitLabRoleConfiguration();

        assertThat(gitLabRoleConfiguration.groups()).containsEntry("group-1", List.of("guest", "owner"));
    }
}